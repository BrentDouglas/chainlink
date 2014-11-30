package io.machinecode.chainlink.transport.core;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.machinecode.chainlink.core.registry.LocalRegistry;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.configuration.RegistryConfiguration;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.marshalling.Cloner;
import io.machinecode.chainlink.spi.marshalling.Marshaller;
import io.machinecode.chainlink.spi.marshalling.MarshallingProvider;
import io.machinecode.chainlink.spi.marshalling.Unmarshaller;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableAndContext;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.util.Pair;
import io.machinecode.chainlink.transport.core.cmd.CleanupCommand;
import io.machinecode.chainlink.transport.core.cmd.DistributedCommand;
import io.machinecode.chainlink.transport.core.cmd.FindExecutableAndContextCommand;
import io.machinecode.chainlink.transport.core.cmd.FindExecutionRepositoryWithIdCommand;
import io.machinecode.chainlink.transport.core.cmd.FindWorkerCommand;
import io.machinecode.chainlink.transport.core.cmd.LeastBusyWorkerCommand;
import io.machinecode.then.api.Deferred;
import io.machinecode.then.api.OnComplete;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.DeferredImpl;
import io.machinecode.then.core.FutureDeferred;
import org.jboss.logging.Logger;

import javax.batch.operations.JobExecutionNotRunningException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public abstract class BaseDistributedRegistry<A, R extends DistributedRegistry<A, R>> extends LocalRegistry implements DistributedRegistry<A, R> {

    private static final Logger log = Logger.getLogger(BaseDistributedRegistry.class);

    protected final Marshaller marshaller;
    protected final Unmarshaller unmarshaller;
    protected final Cloner cloner;
    protected final Executor network;
    protected final Executor reaper;

    final TMap<WorkerId, Worker> remoteWorkers = new THashMap<WorkerId, Worker>();
    final TLongObjectMap<List<Pair<ChainId,A>>> remoteExecutions = new TLongObjectHashMap<List<Pair<ChainId,A>>>();

    protected final long timeout;
    protected final TimeUnit unit;

    public BaseDistributedRegistry(final RegistryConfiguration configuration) throws Exception {
        final MarshallingProvider provider = configuration.getMarshallingProviderFactory().produce(configuration);
        this.marshaller = provider.getMarshaller();
        this.unmarshaller = provider.getUnmarshaller();
        this.cloner = provider.getCloner();

        this.network= Executors.newSingleThreadExecutor();
        this.reaper = Executors.newSingleThreadExecutor();

        this.timeout = Long.parseLong(configuration.getProperty(Constants.TIMEOUT, Constants.Defaults.NETWORK_TIMEOUT));
        this.unit = TimeUnit.valueOf(configuration.getProperty(Constants.TIMEOUT_UNIT, Constants.Defaults.NETWORK_TIMEOUT_UNIT));
    }

    @Override
    protected void onRegisterJob(final long jobExecutionId) {
        remoteExecutions.put(jobExecutionId, new ArrayList<Pair<ChainId, A>>());
    }

    @Override
    protected Promise<?,?,?> onUnregisterJob(final long jobExecutionId, final Chain<?> job) {
        final FutureDeferred<Object, Void> promise = new FutureDeferred<Object, Void>((Future<Object>)job);
        promise.onComplete(new OnComplete() {
            @Override
            public void complete(final int state) {
                for (final Pair<ChainId, A> pair : remoteExecutions.remove(jobExecutionId)) {
                    final A address = pair.getValue();
                    if (!address.equals(getLocal())) {
                        try {
                            _invoke(address, new CleanupCommand<A, R>(jobExecutionId));
                        } catch (final Exception e) {
                            log.errorf(e,""); // TODO Message
                        }
                    }
                }
                log.debugf(Messages.get("CHAINLINK-005101.registry.removed.job"), jobExecutionId);
            }
        });
        this.reaper.execute(promise);
        return promise;
    }

    protected abstract List<A> getRemotes();

    protected abstract DistributedWorker<A, R> createDistributedWorker(final A address, final WorkerId workerId);

    protected abstract DistributedProxyExecutionRepository<A, R> createDistributedExecutionRepository(final ExecutionRepositoryId id, final A address);

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
        A remote = null;
        if (workerId instanceof DistributedWorkerId) {
            remote = ((DistributedWorkerId<A>) workerId).getAddress();
        }
        if (remote != null) {
            if (remote.equals(getLocal())) {
                throw new IllegalStateException(); //This should have been handled at the start
            }
            final Worker rpcWorker = createDistributedWorker(remote, workerId);
            remoteWorkers.put(workerId, rpcWorker);
            return rpcWorker;
        }
        final List<Future<A>> futures = new ArrayList<Future<A>>();
        final List<A> members = this.getRemotes();
        for (final A address : members) {
            try {
                futures.add(_invoke(address, new FindWorkerCommand<A, R>(workerId)));
            } catch (Exception e) {
                log.errorf(e, ""); //TODO Message
            }
        }
        for (final Future<A> future : futures) {
            try {
                //TODO Search these for completes rather that .get() them in order
                final A address = future.get();
                if (address == null) {
                    continue;
                }
                final Worker rpcWorker;
                if (address.equals(getLocal())) {
                    throw new IllegalStateException(); //Also should not have been distributed
                } else {
                    rpcWorker = createDistributedWorker(address, workerId);
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

    @Override
    public List<Worker> getWorkers(final int required) {
        final List<A> members = new ArrayList<A>(this.getRemotes());
        members.add(this.getLocal());
        final List<Future<DistributedWorkerId<A>>> futures = new ArrayList<Future<DistributedWorkerId<A>>>(required);
        for (final A address : filterMembers(members, required)) {
            try {
                futures.add(_invoke(address, new LeastBusyWorkerCommand<A, R>()));
            } catch (final Exception e) {
                log.errorf(e, ""); //TODO Message
            }
        }
        final ArrayList<Worker> workers = new ArrayList<Worker>(required);
        for (final Future<DistributedWorkerId<A>> future : futures) {
            try {
                final DistributedWorkerId<A> threadId = future.get(this.timeout, this.unit);
                if (getLocal().equals(threadId.getAddress())) {
                    workers.add(getWorker(threadId));
                } else {
                    workers.add(createDistributedWorker(threadId.getAddress(), threadId));
                }
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
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
        final List<A> members = this.getRemotes();
        final List<Future<A>> futures = new ArrayList<Future<A>>(members.size());
        for (final A address : members) {
            try {
                futures.add(_invoke(address, new FindExecutionRepositoryWithIdCommand<A, R>(id)));
            } catch (Exception e) {
                log.errorf(e, ""); //TODO Message
            }
        }
        for (final Future<A> future : futures) {
            try {
                final A address = future.get(this.timeout, this.unit);
                if (address == null) {
                    continue;
                } else if (getLocal().equals(address)) {
                    throw new IllegalStateException(); //TODO Message
                }
                return createDistributedExecutionRepository(id, address);
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
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
        final List<A> members = this.getRemotes();
        final List<Future<ExecutableAndContext>> futures = new ArrayList<Future<ExecutableAndContext>>(members.size());
        for (final A address : members) {
            try {
                futures.add(_invoke(address, new FindExecutableAndContextCommand<A, R>(jobExecutionId, id)));
            } catch (Exception e) {
                log.errorf(e, ""); //TODO Message
            }
        }
        for (final Future<ExecutableAndContext> future : futures) {
            try {
                final ExecutableAndContext executable = future.get(this.timeout, this.unit);
                if (executable == null) {
                    continue;
                }
                return executable;
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalStateException(); //TODO Message
    }

    @Override
    public DistributedWorkerId<A> leastBusyWorker() {
        return (DistributedWorkerId<A>)getWorker().id();
    }

    @Override
    public ExecutionRepository getLocalExecutionRepository(final ExecutionRepositoryId id) {
        return super.getExecutionRepository(id);
    }

    @Override
    public boolean hasWorker(final WorkerId workerId) {
        return _localWorker(workerId) != null;
    }

    protected <T> Future<T> _invoke(final A address, final DistributedCommand<T, A, R> command) throws Exception {
        final Deferred<T,Throwable,Void> promise = new DeferredImpl<T, Throwable,Void>();
        invoke(address, command, promise);
        return promise;
    }

    private Worker _localWorker(final WorkerId workerId) {
        return super.getWorker(workerId);
    }

    //TODO
    protected List<A> filterMembers(final List<A> all, final int required) {
        return all.subList(0, required > all.size() ? all.size() : required);
    }

    protected List<A> _remoteMembers(final Collection<A> all) {
        final List<A> that = new ArrayList<A>(all);
        that.remove(this.getLocal());
        return Collections.unmodifiableList(that);
    }
}
