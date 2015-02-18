package io.machinecode.chainlink.core.transport;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.core.execution.CallbackEventImpl;
import io.machinecode.chainlink.core.execution.ExecutableEventImpl;
import io.machinecode.chainlink.core.registry.UUIDId;
import io.machinecode.chainlink.core.then.AllChain;
import io.machinecode.chainlink.core.then.ChainImpl;
import io.machinecode.chainlink.core.transport.cmd.CleanupCommand;
import io.machinecode.chainlink.core.transport.cmd.Command;
import io.machinecode.chainlink.core.transport.cmd.FindWorkerIdForExecutionCommand;
import io.machinecode.chainlink.core.transport.cmd.GetWorkerIdsCommand;
import io.machinecode.chainlink.core.transport.cmd.PushChainCommand;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.execution.WorkerId;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.JobEventListener;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.registry.RepositoryId;
import io.machinecode.chainlink.spi.repository.Repository;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.then.api.Deferred;
import io.machinecode.then.api.OnComplete;
import io.machinecode.then.api.Promise;
import io.machinecode.then.api.Reject;
import io.machinecode.then.api.Resolve;
import io.machinecode.then.core.FutureDeferred;
import io.machinecode.then.core.When;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class DistributedTransport<A> implements Transport {

    private static final Logger log = Logger.getLogger(DistributedTransport.class);

    protected final Registry registry;

    protected Configuration configuration;

    protected final Executor network;
    protected final Executor reaper;

    final TLongObjectMap<List<A>> remoteExecutions = new TLongObjectHashMap<>();

    protected final long timeout;
    protected final TimeUnit unit;

    public DistributedTransport(final Dependencies dependencies, final Properties properties) throws Exception {
        this.registry = dependencies.getRegistry();

        this.network= Executors.newCachedThreadPool();
        this.reaper = Executors.newSingleThreadExecutor();

        this.timeout = Long.parseLong(properties.getProperty(Constants.TIMEOUT, Constants.Defaults.NETWORK_TIMEOUT));
        this.unit = TimeUnit.valueOf(properties.getProperty(Constants.TIMEOUT_UNIT, Constants.Defaults.NETWORK_TIMEOUT_UNIT));

        this.registry.registerJobEventListener("cleanup-remote-jobs", new JobEventListener() {
            @Override
            public Promise<?,?,?> onRegister(final long jobExecutionId, final Chain<?> job) {
                remoteExecutions.put(jobExecutionId, new ArrayList<A>());
                return null;
            }

            @Override
            public Promise<?,?,?> onUnregister(final long jobExecutionId, final Chain<?> job) {
                final CleanupCommand command = new CleanupCommand(jobExecutionId);
                final FutureDeferred<Object, Void> promise = new FutureDeferred<>(job, timeout, unit);
                promise.onComplete(new OnComplete() {
                    @Override
                    public void complete(final int status) {
                        for (final A address : remoteExecutions.remove(jobExecutionId)) {
                            if (!address.equals(getAddress())) {
                                try {
                                    invokeRemote(address, command);
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
    public void open(final Configuration configuration) throws Exception {
        this.configuration = configuration;
    }

    @Override
    public void close() throws Exception {
        this.registry.unregisterJobEventListener("cleanup-remote-jobs");
    }

    @Override
    public Promise<Chain<?>,Throwable,Object> distribute(final int maxThreads, final Executable... executables) throws Exception {
        if (executables.length == 0) {
            throw new IllegalArgumentException(); //TODO Message
        }
        final long jobExecutionId = executables[0].getContext().getJobExecutionId();
        return _getWorkers(jobExecutionId, maxThreads).then(new Reject<List<RemoteExecution>, Throwable, Chain<?>, Throwable, Object>() {
            @Override
            public void resolve(final List<RemoteExecution> that, final Deferred<Chain<?>, Throwable, Object> next) {
                ListIterator<RemoteExecution> it = that.listIterator();
                final Chain<?>[] chains = new Chain[executables.length];
                int i = 0;
                for (final Executable executable : executables) {
                    if (!it.hasNext()) {
                        it = that.listIterator();
                    }
                    final RemoteExecution remote = it.next();
                    final int index = i++;
                    chains[index] = remote.getChain();
                    registry.registerChain(jobExecutionId, remote.getLocalId(), remote.getChain());
                    final Worker worker = remote.getWorker();
                    worker.execute(new ExecutableEventImpl(executable, remote.getRemoteId()));
                }
                next.resolve(new AllChain<Executable>(chains));
            }

            @Override
            public void reject(final Throwable that, final Deferred<Chain<?>, Throwable, Object> next) {
                try {
                    next.resolve(LocalTransport.localDistribute(configuration, maxThreads, executables));
                } catch (final Exception e) {
                    e.addSuppressed(that);
                    next.reject(e);
                }
            }
        });
    }

    @Override
    public Promise<Chain<?>,Throwable,Object> callback(final ExecutableId executableId, final ExecutionContext context) throws Exception {
        final long jobExecutionId = context.getJobExecutionId();
        return fetchWorker(jobExecutionId, executableId).then(new Reject<RemoteExecution, Throwable, Chain<?>, Throwable, Object>() {
            @Override
            public void resolve(final RemoteExecution that, final Deferred<Chain<?>,Throwable,Object> next) {
                final Worker worker = that.getWorker();
                registry.registerChain(jobExecutionId, that.getLocalId(), that.getChain());
                worker.callback(new CallbackEventImpl(jobExecutionId, executableId, that.getRemoteId(), context));
                next.resolve(that.getChain());
            }

            @Override
            public void reject(final Throwable that, final Deferred<Chain<?>, Throwable, Object> next) {
                try {
                    next.resolve(LocalTransport.localCallback(configuration, executableId, context));
                } catch (final Exception e) {
                    e.addSuppressed(that);
                    next.reject(e);
                }
            }
        });
    }

    @Override
    public Repository getRepository(final RepositoryId id) throws Exception {
        final Repository ours = configuration.getRegistry().getRepository(id);
        if (ours != null) {
            return ours;
        }
        return new DistributedProxyRepository(this, id);
    }

    protected Promise<RemoteExecution,? extends Throwable,Object> fetchWorker(final long jobExecutionId, final ExecutableId executableId) throws Exception {
        final Executable executable = configuration.getRegistry().getExecutable(jobExecutionId, executableId);
        if (executable != null) {
            final Worker worker = configuration.getExecutor().getWorker(executable.getWorkerId());
            if (worker == null) {
                return When.rejected(new Exception("No worker found with jobExecutionId=" + jobExecutionId + " and executableId" + executableId));
            }
            final UUIDId id = new UUIDId(this);
            return When.resolved(new RemoteExecution(worker, id, id, new ChainImpl<Void>()));
        }
        return _getWorker(jobExecutionId, executableId);
    }

    protected final <T> Promise<T, Throwable, Object> invokeRemote(final Object address, final Command<T> command) {
        return invokeRemote(address, command, timeout, unit);
    }

    protected abstract <T> Promise<T,Throwable,Object> invokeRemote(final Object address, final Command<T> command, final long timeout, final TimeUnit unit);

    private Promise<RemoteExecution,Throwable,?> getRemoteChainAndIds(final WorkerId workerId, final long jobExecutionId) {
        final ChainId localId = new UUIDId(this);
        return invokeRemote(
                workerId.getAddress(),
                new PushChainCommand(jobExecutionId, localId),
                getTimeout(),
                getTimeUnit()
        ).then(new Resolve<ChainId, RemoteExecution, Throwable, Object>() {
            @Override
            public void resolve(final ChainId remoteId, final Deferred<RemoteExecution, Throwable, Object> next) {
                next.resolve(
                        new RemoteExecution(
                                new DistributedWorker(DistributedTransport.this, workerId),
                                localId,
                                remoteId,
                                new DistributedLocalChain(DistributedTransport.this, workerId.getAddress(), jobExecutionId, remoteId)
                        )
                );
            }
        });
    }

    protected Promise<RemoteExecution,Throwable,Object> _getWorker(final long jobExecutionId, final ExecutableId executableId) {
        return invokeEverywhere(new FindWorkerIdForExecutionCommand(jobExecutionId, executableId)).then(new Resolve<Iterable<WorkerId>, RemoteExecution, Throwable, Object>() {
            @Override
            public void resolve(final Iterable<WorkerId> that, final Deferred<RemoteExecution, Throwable, Object> next) {
                for (final WorkerId id : that) {
                    if (id != null) {
                        getRemoteChainAndIds(id, jobExecutionId)
                                .onResolve(next)
                                .onReject(next)
                                .onCancel(next);
                        return;
                    }
                }
                next.reject(null);
            }
        });
    }

    protected Promise<List<RemoteExecution>,Throwable,Object> _getWorkers(final long jobExecutionId, final int required) {
        return invokeEverywhere(new GetWorkerIdsCommand(required)).then(new Resolve<Iterable<Iterable<WorkerId>>, List<RemoteExecution>, Throwable, Object>() {
            @Override
            public void resolve(final Iterable<Iterable<WorkerId>> that, final Deferred<List<RemoteExecution>, Throwable, Object> next) {
                int i = 0;
                final List<Promise<RemoteExecution, Throwable, ?>> promises = new ArrayList<>(required);
                loop: while (i < required) {
                    for (final Iterable<WorkerId> node : that) {
                        for (final WorkerId workerId : node) {
                            promises.add(getRemoteChainAndIds(workerId, jobExecutionId));
                            ++i;
                            if (i >= required) {
                                break loop;
                            }
                        }
                    }
                    if (i == 0) {
                        next.reject(new Exception("No remote workers found"));
                        return;
                    }
                }
                When.all(promises)
                        .onResolve(next)
                        .onReject(next)
                        .onCancel(next);
            }
        });
    }

    public abstract A getAddress();

    protected abstract <T> Promise<? extends Iterable<T>,Throwable,Object> invokeEverywhere(final Command<T> command);

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public TimeUnit getTimeUnit() {
        return unit;
    }

    protected List<A> _remoteMembers(final Collection<A> all) {
        final List<A> that = new ArrayList<>(all);
        that.remove(this.getAddress());
        return Collections.unmodifiableList(that);
    }
}
