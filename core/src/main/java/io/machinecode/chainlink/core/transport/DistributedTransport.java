package io.machinecode.chainlink.core.transport;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.machinecode.chainlink.core.transport.cmd.CleanupCommand;
import io.machinecode.chainlink.core.transport.cmd.FindExecutableCommand;
import io.machinecode.chainlink.core.transport.cmd.FindExecutionRepositoryWithIdCommand;
import io.machinecode.chainlink.core.transport.cmd.FindWorkerByIdCommand;
import io.machinecode.chainlink.core.transport.cmd.FindWorkerForExecutionCommand;
import io.machinecode.chainlink.core.transport.cmd.LeastBusyWorkerCommand;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.marshalling.Marshalling;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.JobEventListener;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.util.Pair;
import io.machinecode.then.api.Deferred;
import io.machinecode.then.api.OnComplete;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.DeferredImpl;
import io.machinecode.then.core.FutureDeferred;
import org.jboss.logging.Logger;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class DistributedTransport<A> extends BaseTransport<A> {

    private static final Logger log = Logger.getLogger(DistributedTransport.class);

    protected final WeakReference<ClassLoader> loader;
    protected final Marshalling marshalling;
    protected final Executor network;
    protected final Executor reaper;

    final TMap<WorkerId, Worker> remoteWorkers = new THashMap<>();
    final TLongObjectMap<List<Pair<ChainId,A>>> remoteExecutions = new TLongObjectHashMap<>();

    protected final long timeout;
    protected final TimeUnit unit;

    public DistributedTransport(final Dependencies dependencies, final Properties properties) throws Exception {
        super(dependencies, properties);
        this.loader = new WeakReference<>(dependencies.getClassLoader());
        this.marshalling = dependencies.getMarshalling();

        this.network= Executors.newCachedThreadPool();
        this.reaper = Executors.newSingleThreadExecutor();

        this.timeout = Long.parseLong(properties.getProperty(Constants.TIMEOUT, Constants.Defaults.NETWORK_TIMEOUT));
        this.unit = TimeUnit.valueOf(properties.getProperty(Constants.TIMEOUT_UNIT, Constants.Defaults.NETWORK_TIMEOUT_UNIT));

        this.getRegistry().registerJobEventListener("cleanup-remote-jobs", new JobEventListener() {
            @Override
            public Promise<?, ?, ?> onRegister(final long jobExecutionId, final Chain<?> job) {
                remoteExecutions.put(jobExecutionId, new ArrayList<Pair<ChainId, A>>());
                return null;
            }

            @Override
            public Promise<?, ?, ?> onUnregister(final long jobExecutionId, final Chain<?> job) {
                final CleanupCommand<A> command = new CleanupCommand<>(jobExecutionId);
                final FutureDeferred<Object, Void> promise = new FutureDeferred<>(job, timeout, unit);
                promise.onComplete(new OnComplete() {
                    @Override
                    public void complete(final int status) {
                        for (final Pair<ChainId, A> pair : remoteExecutions.remove(jobExecutionId)) {
                            final A address = pair.getValue();
                            if (!address.equals(getAddress())) {
                                try {
                                    _invoke(address, command);
                                } catch (Exception e) {
                                    log.errorf(e, ""); // TODO Message
                                }
                            }
                        }
                        log.debugf(Messages.get("CHAINLINK-005101.registry.removed.job"), jobExecutionId);
                    }
                });
                reaper.execute(promise);
                return promise;
            }
        });
    }

    @Override
    public void close() throws Exception {
        this.getRegistry().unregisterJobEventListener("cleanup-remote-jobs");
        super.close();
    }

    protected abstract List<A> getRemotes();

    protected abstract DistributedWorker<A> createDistributedWorker(final A address, final WorkerId workerId);

    protected abstract DistributedProxyExecutionRepository<A> createDistributedExecutionRepository(final ExecutionRepositoryId id, final A address);

    protected abstract boolean isMatchingAddressType(final Object address);

    @Override
    public Worker getWorker(final WorkerId workerId) throws InterruptedException, ExecutionException, TimeoutException {
        final Worker worker = getLocalWorker(workerId);
        if (worker != null) {
            return worker;
        }
        final Worker remoteWorker = remoteWorkers.get(workerId);
        if (remoteWorker != null) {
            return remoteWorker;
        }
        final Object addr = workerId.getAddress();
        if (addr != null && isMatchingAddressType(addr)) {
            @SuppressWarnings("unchecked")
            final A remote = (A)addr;
            if (remote.equals(getAddress())) {
                throw new IllegalStateException(); //This should have been handled at the start
            }
            final Worker rpcWorker = createDistributedWorker(remote, workerId);
            remoteWorkers.put(workerId, rpcWorker);
            return rpcWorker;
        }
        final List<Future<A>> futures = new ArrayList<>();
        final List<A> members = this.getRemotes();
        for (final A address : members) {
            try {
                futures.add(_invoke(address, new FindWorkerByIdCommand<A>(workerId)));
            } catch (Exception e) {
                log.errorf(e, ""); //TODO Message
            }
        }
        for (final Future<A> future : futures) {
            //TODO Search these for completes rather that .get(...) them in order
            final A address = future.get(this.timeout, this.unit);
            if (address == null) {
                continue;
            }
            final Worker rpcWorker;
            if (address.equals(getAddress())) {
                throw new IllegalStateException(); //Also should not have been distributed
            } else {
                rpcWorker = createDistributedWorker(address, workerId);
                remoteWorkers.put(workerId, rpcWorker);
            }
            return rpcWorker;
        }
        throw new IllegalStateException(); //TODO message
    }

    @Override
    public List<Worker> getWorkers(final int required) throws InterruptedException, ExecutionException, TimeoutException {
        final List<A> members = new ArrayList<>(this.getRemotes());
        members.add(this.getAddress());
        final List<Future<WorkerId>> futures = new ArrayList<>(required);
        for (final A address : filterMembers(members, required)) {
            try {
                futures.add(_invoke(address, new LeastBusyWorkerCommand<A>()));
            } catch (final Exception e) {
                log.errorf(e, ""); //TODO Message
            }
        }
        final ArrayList<Worker> workers = new ArrayList<>(required);
        for (final Future<WorkerId> future : futures) {
            final WorkerId threadId = future.get(this.timeout, this.unit);
            if (getAddress().equals(threadId.getAddress())) {
                workers.add(getLocalWorker(threadId));
            } else {
                final Object addr = threadId.getAddress();
                if (isMatchingAddressType(addr)) {
                    @SuppressWarnings("unchecked")
                    final A remote = (A)addr;
                    workers.add(createDistributedWorker(remote, threadId));
                }
            }
        }
        return workers;
    }

    @Override
    public Worker getWorker(final long jobExecutionId, final ExecutableId executableId) throws InterruptedException, ExecutionException, TimeoutException {
        final Worker worker = getLocalWorker(jobExecutionId, executableId);
        if (worker != null) {
            return worker;
        }
        final List<A> members = new ArrayList<>(this.getRemotes());
        final List<Future<WorkerIdAndAddress<A>>> futures = new ArrayList<>();
        for (final A address : members) {
            try {
                futures.add(_invoke(address, new FindWorkerForExecutionCommand<A>(jobExecutionId, executableId)));
            } catch (final Exception e) {
                log.errorf(e, ""); //TODO Message
            }
        }
        for (final Future<WorkerIdAndAddress<A>> future : futures) {
                //TODO Search these for completes rather that .get(...) them in order
            final WorkerIdAndAddress<A> that = future.get(this.timeout, this.unit);
            if (that == null) {
                continue;
            }
            final Worker rpcWorker;
            if (that.getAddress().equals(getAddress())) {
                throw new IllegalStateException(); //Also should not have been distributed
            } else {
                rpcWorker = createDistributedWorker(that.getAddress(), that.getWorkerId());
                remoteWorkers.put(that.getWorkerId(), rpcWorker);
            }
            return rpcWorker;
        }
        throw new IllegalStateException(); //TODO message
    }

    @Override
    public ExecutionRepository getExecutionRepository(final ExecutionRepositoryId id) throws InterruptedException, ExecutionException, TimeoutException {
        final ExecutionRepository ours = getRegistry().getExecutionRepository(id);
        if (ours != null) {
            return ours;
        }
        final List<A> members = this.getRemotes();
        final List<Future<A>> futures = new ArrayList<>(members.size());
        for (final A address : members) {
            try {
                futures.add(_invoke(address, new FindExecutionRepositoryWithIdCommand<A>(id)));
            } catch (Exception e) {
                log.errorf(e, ""); //TODO Message
            }
        }
        for (final Future<A> future : futures) {
            final A address = future.get(this.timeout, this.unit);
            if (address == null) {
                continue;
            } else if (getAddress().equals(address)) {
                throw new IllegalStateException(); //TODO Message
            }
            return createDistributedExecutionRepository(id, address);
        }
        throw new IllegalStateException(); //TODO Message
    }

    @Override
    public Executable getExecutable(final long jobExecutionId, final ExecutableId id) throws InterruptedException, ExecutionException, TimeoutException {
        final Executable ours = this.getRegistry().getExecutable(jobExecutionId, id);
        if (ours != null) {
            return ours;
        }
        final List<A> members = this.getRemotes();
        final List<Future<Executable>> futures = new ArrayList<>(members.size());
        for (final A address : members) {
            try {
                futures.add(_invoke(address, new FindExecutableCommand<A>(jobExecutionId, id)));
            } catch (Exception e) {
                log.errorf(e, ""); //TODO Message
            }
        }
        for (final Future<Executable> future : futures) {
            final Executable executable = future.get(this.timeout, this.unit);
            if (executable == null) {
                continue;
            }
            return executable;
        }
        throw new IllegalStateException(); //TODO Message
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public TimeUnit getTimeUnit() {
        return unit;
    }

    protected <T> Future<T> _invoke(final A address, final Command<T, A> command) throws Exception {
        final Deferred<T,Throwable,Void> promise = new DeferredImpl<>();
        invokeRemote(address, command, promise, this.timeout, this.unit);
        return promise;
    }

    //TODO
    protected List<A> filterMembers(final List<A> all, final int required) {
        return all.subList(0, required > all.size() ? all.size() : required);
    }

    protected List<A> _remoteMembers(final Collection<A> all) {
        final List<A> that = new ArrayList<>(all);
        that.remove(this.getAddress());
        return Collections.unmodifiableList(that);
    }
}
