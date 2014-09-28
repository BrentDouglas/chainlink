package io.machinecode.chainlink.transport.jgroups;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.machinecode.chainlink.core.registry.LocalRegistry;
import io.machinecode.chainlink.core.then.WhenImpl;
import io.machinecode.chainlink.spi.configuration.RegistryConfiguration;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableAndContext;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.serialization.Serializer;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.then.When;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.util.Pair;
import io.machinecode.chainlink.transport.jgroups.cmd.CleanupCommand;
import io.machinecode.chainlink.transport.jgroups.cmd.Command;
import io.machinecode.chainlink.transport.jgroups.cmd.FindExecutableAndContextCommand;
import io.machinecode.chainlink.transport.jgroups.cmd.FindExecutionRepositoryWithIdCommand;
import io.machinecode.chainlink.transport.jgroups.cmd.FindWorkerCommand;
import io.machinecode.chainlink.transport.jgroups.cmd.LeastBusyWorkerCommand;
import io.machinecode.then.api.OnComplete;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.PromiseImpl;
import org.jboss.logging.Logger;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.MembershipListener;
import org.jgroups.Message;
import org.jgroups.View;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestHandler;
import org.jgroups.blocks.RequestOptions;

import javax.batch.operations.JobExecutionNotRunningException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JGroupsRegistry extends LocalRegistry implements RequestHandler, MembershipListener {

    private static final Logger log = Logger.getLogger(JGroupsRegistry.class);

    final Serializer serializer;
    final When network;
    final When reaper;

    final JChannel channel;
    final MessageDispatcher dispatcher;
    final Address local;
    protected volatile List<Address> remotes;

    final TMap<WorkerId, Worker> remoteWorkers = new THashMap<WorkerId, Worker>();
    final TLongObjectMap<List<Pair<ChainId,Address>>> remoteExecutions = new TLongObjectHashMap<List<Pair<ChainId,Address>>>();

    public JGroupsRegistry(final RegistryConfiguration configuration, final JChannel channel, final String clusterName) throws Exception {
        this.serializer = configuration.getSerializerFactory().produce(configuration);
        this.channel = channel;
        this.network= configuration.getWhenFactory().produce(configuration);
        this.reaper = configuration.getWhenFactory().produce(configuration);
        try {
            channel.connect(clusterName);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        this.local = channel.getAddress();
        this.dispatcher = new MessageDispatcher(channel, null, this);
        this.dispatcher.setRequestHandler(this);
    }

    @Override
    public void startup() {
        super.startup();
        this.remotes = _remoteMembers(this.channel.getView().getMembers());
        log.infof("JGroupsRegistry started on address: [%s]", this.local); //TODO Message
    }

    @Override
    public void shutdown() {
        log.infof("JGroupsRegistry is shutting down."); //TODO Message
        this.channel.close();
        super.shutdown();
    }

    @Override
    protected void onRegisterJob(final long jobExecutionId) {
        remoteExecutions.put(jobExecutionId, new ArrayList<Pair<ChainId, Address>>());
    }

    @Override
    protected Promise<?,?> onUnregisterJob(final long jobExecutionId, final Chain<?> job) {
        final Promise<Object, Throwable> promise = new PromiseImpl<Object, Throwable>().onComplete(new OnComplete() {
            @Override
            public void complete() {
                for (final Pair<ChainId, Address> pair : remoteExecutions.remove(jobExecutionId)) {
                    final Address address = pair.getValue();
                    if (!address.equals(local)) {
                        try {
                            _invoke(address, new CleanupCommand(jobExecutionId));
                        } catch (Exception e) {
                            // TODO
                        }
                    }
                }
                log.debugf(Messages.get("CHAINLINK-005101.registry.removed.job"), jobExecutionId);
            }
        });
        this.reaper.when((Future<Object>) job, promise);
        return promise;
    }

    @Override
    public ChainId generateChainId() {
        return new JGroupsUUIDId(local);
    }

    @Override
    public ExecutableId generateExecutableId() {
        return new JGroupsUUIDId(local);
    }

    @Override
    public WorkerId generateWorkerId(final Worker worker) {
        if (worker instanceof Thread) {
            return new JGroupsThreadId((Thread)worker, local);
        } else {
            return new JGroupsUUIDId(local);
        }
    }

    @Override
    public ExecutionRepositoryId generateExecutionRepositoryId() {
        return new JGroupsUUIDId(local);
    }

    private Worker _localWorker(final WorkerId workerId) {
        return super.getWorker(workerId);
    }

    @Override
    public Worker getWorker(final WorkerId workerId) {
        final Worker worker = _localWorker(workerId);
        if (worker != null) {
            return worker;
        }
        final Worker remoteWorker = remoteWorkers.get(workerId);
        if (remoteWorker != null) {
            return remoteWorker;
        }
        Address remote = null;
        if (workerId instanceof JGroupsThreadId) {
            remote = ((JGroupsThreadId) workerId).address;
        }
        if (remote != null) {
            if (remote.equals(local)) {
                throw new IllegalStateException(); //This should have been handled at the start
            }
            final Worker rpcWorker = new JGroupsWorker(this, local, remote, workerId);
            remoteWorkers.put(workerId, rpcWorker);
            return rpcWorker;
        }
        final List<Future<Address>> futures = new ArrayList<Future<Address>>();
        final List<Address> members = this.remotes;
        for (final Address address : members) {
            try {
                futures.add(_invoke(address, new FindWorkerCommand(workerId)));
            } catch (Exception e) {
                log.errorf(e, ""); //TODO Message
            }
        }
        for (final Future<Address> future : futures) {
            try {
                //TODO Search these for completes rather that .get() them in order
                final Address address = future.get();
                if (address == null) {
                    continue;
                }
                final Worker rpcWorker;
                if (address.equals(local)) {
                    throw new IllegalStateException(); //Also should not have been distributed
                } else {
                    rpcWorker = new JGroupsWorker(this, local, address, workerId);
                    remoteWorkers.put(workerId, rpcWorker);
                }
                return rpcWorker;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalStateException(); //TODO message
    }

    private List<Worker> _localWorkers(final int required) {
        return super.getWorkers(required);
    }

    @Override
    public List<Worker> getWorkers(final int required) {
        final List<Address> members = new ArrayList<Address>(this.remotes);
        members.add(this.local);
        final List<Future<JGroupsThreadId>> futures = new ArrayList<Future<JGroupsThreadId>>(required);
        for (final Address address : filterMembers(members, required)) {
            try {
                futures.add(_invoke(address, new LeastBusyWorkerCommand()));
            } catch (final Exception e) {
                log.errorf(e, ""); //TODO Message
            }
        }
        final ArrayList<Worker> workers = new ArrayList<Worker>(required);
        for (final Future<JGroupsThreadId> future : futures) {
            try {
                final JGroupsThreadId threadId = future.get();
                if (local.equals(threadId.address)) {
                    workers.add(getWorker(threadId));
                } else {
                    workers.add(new JGroupsWorker(this, local, threadId.address, threadId));
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return workers;
    }

    @Override
    public Chain<?> getJob(final long jobExecutionId) throws JobExecutionNotRunningException {
        //Todo Remote
        return super.getJob(jobExecutionId);
    }

    @Override
    public ExecutionRepository getExecutionRepository(final ExecutionRepositoryId id) {
        final ExecutionRepository ours = super.getExecutionRepository(id);
        if (ours != null) {
            return ours;
        }
        final List<Address> members = this.remotes;
        final List<Future<Address>> futures = new ArrayList<Future<Address>>(members.size());
        for (final Address address : members) {
            try {
                futures.add(_invoke(address, new FindExecutionRepositoryWithIdCommand(id)));
            } catch (Exception e) {
                log.errorf(e, ""); //TODO Message
            }
        }
        for (final Future<Address> future : futures) {
            try {
                final Address address = future.get();
                if (address == null) {
                    continue;
                } else if (local.equals(address)) {
                    throw new IllegalStateException(); //TODO Message
                }
                return new JGroupsRemoteExecutionRepository(this, id, address);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalStateException(); //TODO Message
    }

    @Override
    public ExecutableAndContext getExecutableAndContext(final long jobExecutionId, final ExecutableId id) {
        final ExecutableAndContext ours = super.getExecutableAndContext(jobExecutionId, id);
        if (ours != null) {
            return ours;
        }
        final List<Address> members = this.remotes;
        final List<Future<ExecutableAndContext>> futures = new ArrayList<Future<ExecutableAndContext>>(members.size());
        for (final Address address : members) {
            try {
                futures.add(_invoke(address, new FindExecutableAndContextCommand(jobExecutionId, id)));
            } catch (Exception e) {
                log.errorf(e, ""); //TODO Message
            }
        }
        for (final Future<ExecutableAndContext> future : futures) {
            try {
                final ExecutableAndContext executable = future.get();
                if (executable == null) {
                    continue;
                }
                return executable;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalStateException(); //TODO Message
    }

    public ExecutionRepository getLocalExecutionRepository(final ExecutionRepositoryId id) {
        return  super.getExecutionRepository(id);
    }

    //TODO
    protected List<Address> filterMembers(final List<Address> all, final int required) {
        return all.subList(0, required > all.size() ? all.size() : required);
    }

    public JGroupsThreadId leastBusyWorker() {
        return (JGroupsThreadId)getWorker().id();
    }

    public Address getLocal() {
        return local;
    }

    public boolean hasWorker(final WorkerId workerId) {
        return _localWorker(workerId) != null;
    }

    @Override
    public Object handle(final Message msg) throws Exception {
        final Command<?> command = serializer.read(msg.getBuffer(), Command.class);
        try {
            return command.invoke(this, msg.src());
        } catch (final Exception e) {
            throw e;
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void invoke(final Address address, final Command command, final Promise<T,Throwable> promise) {
        try {
            this.network.when(
                    this.dispatcher.<T>sendMessageWithFuture(
                            new Message(address, serializer.bytes(command)),
                            RequestOptions.SYNC()
                    ),
                    promise
            );
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    public <T> void invoke(final Address address, final Command<T> command, final Promise<T,Throwable> promise, final long timeout, final TimeUnit unit) {
        try {
            this.network.when(
                    this.dispatcher.<T>sendMessageWithFuture(
                            new Message(address, serializer.bytes(command)),
                            RequestOptions.SYNC().setTimeout(unit.toMillis(timeout))
                    ),
                    promise
            );
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    private <T> Future<T> _invoke(final Address address, final Command<T> command) throws Exception {
        return this.dispatcher.sendMessageWithFuture(
                new Message(address, serializer.bytes(command)),
                RequestOptions.SYNC()
        );
    }

    @Override
    public void viewAccepted(final View view) {
        final List<Address> members = this.remotes = _remoteMembers(view.getMembers());
        final StringBuilder builder = new StringBuilder();
        builder.append("[").append(this.local).append("]");
        for (final Address member : members) {
            builder.append(" | ").append(member);
        }
        log.infof("Cluster: %s", builder); //TODO Message
    }

    private List<Address> _remoteMembers(final List<Address> all) {
        final List<Address> that = new ArrayList<Address>(all);
        that.remove(this.local);
        return Collections.unmodifiableList(that);
    }

    @Override
    public void suspect(final Address suspected) {}

    @Override
    public void block() {}

    @Override
    public void unblock() {}
}
