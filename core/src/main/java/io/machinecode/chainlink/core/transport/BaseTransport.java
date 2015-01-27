package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.core.execution.CallbackEventImpl;
import io.machinecode.chainlink.core.execution.ExecutableEventImpl;
import io.machinecode.chainlink.core.registry.UUIDId;
import io.machinecode.chainlink.core.then.AllChain;
import io.machinecode.chainlink.core.then.ChainImpl;
import io.machinecode.chainlink.core.transport.cmd.Command;
import io.machinecode.chainlink.core.transport.cmd.FindWorkerIdForExecutionCommand;
import io.machinecode.chainlink.core.transport.cmd.GetWorkerIdsCommand;
import io.machinecode.chainlink.core.transport.cmd.PushChainCommand;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.execution.WorkerId;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.then.api.Deferred;
import io.machinecode.then.api.Promise;
import io.machinecode.then.api.Reject;
import io.machinecode.then.api.Resolve;
import io.machinecode.then.core.RejectedDeferred;
import io.machinecode.then.core.ResolvedDeferred;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public abstract class BaseTransport<A> implements Transport {

    protected final Registry registry;

    protected final ExecutorService executor;
    protected Configuration configuration;

    public BaseTransport(final Dependencies dependencies, final Properties properties) {
        this.registry = dependencies.getRegistry();
        this.executor = Executors.newCachedThreadPool();
    }

    @Override
    public void open(final Configuration configuration) throws Exception {
        this.configuration = configuration;
    }

    @Override
    public void close() throws Exception {
        //
    }

    @Override
    public Promise<Chain<?>,Throwable,Object> distribute(final int maxThreads, final Executable... executables) throws Exception {
        return _getWorkers(maxThreads).then(new Reject<List<RemoteExecution>, Throwable, Chain<?>, Throwable, Object>() {
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
                    final long jobExecutionId = executable.getContext().getJobExecutionId();
                    chains[index] = remote.getChain();
                    registry.registerChain(jobExecutionId, remote.getLocalId(), remote.getChain());
                    final Worker worker = remote.getWorker();
                    worker.execute(new ExecutableEventImpl(executable, remote.getRemoteId()));
                }
                next.resolve(new AllChain<Executable>(chains));
            }

            // If looking for remotes failed we fall back to using only local workers
            @Override
            public void reject(final Throwable that, final Deferred<Chain<?>, Throwable, Object> next) {
                try {
                    final List<Worker> ret = configuration.getExecutor().getWorkers(maxThreads);
                    ListIterator<Worker> it = ret.listIterator();
                    final Chain<?>[] chains = new Chain[executables.length];
                    int i = 0;
                    for (final Executable executable : executables) {
                        if (!it.hasNext()) {
                            it = ret.listIterator();
                        }
                        final Chain<?> chain = new ChainImpl<Void>();
                        final ChainId chainId = new UUIDId(BaseTransport.this);
                        registry.registerChain(executable.getContext().getJobExecutionId(), chainId, chain);
                        final Worker worker = it.next();
                        worker.execute(new ExecutableEventImpl(executable, chainId));
                        chains[i++] = chain;
                    }
                    next.resolve(new AllChain<Executable>(chains));
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
                    next.resolve(configuration.getExecutor().callback(executableId, context));
                } catch (final Exception e) {
                    e.addSuppressed(that);
                    next.reject(e);
                }
            }
        });
    }

    @Override
    public ExecutionRepository getExecutionRepository(final ExecutionRepositoryId id) throws Exception {
        final ExecutionRepository ours = configuration.getRegistry().getExecutionRepository(id);
        if (ours != null) {
            return ours;
        }
        return new DistributedProxyExecutionRepository(this, id);
    }

    private Promise<RemoteExecution,Throwable,Object> fetchWorker(final long jobExecutionId, final ExecutableId executableId) throws Exception {
        final Executable executable = configuration.getRegistry().getExecutable(jobExecutionId, executableId);
        if (executable != null) {
            final Worker worker = configuration.getExecutor().getWorker(executable.getWorkerId());
            if (worker == null) {
                return new RejectedDeferred<RemoteExecution, Throwable, Object>(new Exception("No worker found with jobExecutionId=" + jobExecutionId + " and executableId" + executableId));
            }
            final UUIDId id = new UUIDId(this);
            return new ResolvedDeferred<RemoteExecution, Throwable, Object>(new RemoteExecutionImpl(worker, id, id, new ChainImpl<Void>()));
        }
        return _getWorker(jobExecutionId, executableId);
    }

    public <T> Future<T> invokeLocal(final Command<T> command, final A origin) throws Exception {
        final BaseTransport<?> self = this;
        return executor.submit(new Callable<T>() {
            @Override
            public T call() throws Exception {
                try {
                    return command.perform(self.configuration, origin);
                } catch (final Throwable e) {
                    throw new Exception(e);
                }
            }
        });
    }

    public <T> Promise<T, Throwable, Object> invokeRemote(final Object address, final Command<T> command) {
        return invokeRemote(address, command, getTimeout(), getTimeUnit());
    }

    public abstract <T> Promise<T,Throwable,Object> invokeRemote(final Object address, final Command<T> command, final long timeout, final TimeUnit unit);

    protected <T> Promise<? extends Iterable<T>,Throwable,Object> invokeEverywhere(final Command<T> command) {
        return invokeEverywhere(command, getTimeout(), getTimeUnit());
    }

    protected abstract <T> Promise<? extends Iterable<T>,Throwable,Object> invokeEverywhere(final Command<T> command, final long timeout, final TimeUnit unit);

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
                        new RemoteExecutionImpl(
                                new DistributedWorker(BaseTransport.this, workerId),
                                localId,
                                remoteId,
                                new DistributedLocalChain(BaseTransport.this, workerId.getAddress(), jobExecutionId, remoteId)
                        )
                );
            }
        });
    }

    private Promise<RemoteExecution,Throwable,Object> _getWorker(final long jobExecutionId, final ExecutableId executableId) {
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

    private Promise<List<RemoteExecution>,Throwable,Object> _getWorkers(final int required) {
        return invokeEverywhere(new GetWorkerIdsCommand(required)).then(new Resolve<Iterable<Iterable<WorkerId>>, List<RemoteExecution>, Throwable, Object>() {
            @Override
            public void resolve(final Iterable<Iterable<WorkerId>> that, final Deferred<List<RemoteExecution>, Throwable, Object> next) {
                final List<RemoteExecution> ret = new ArrayList<>(required);
                int i = 0;
                while (i < required) {
                    for (final Iterable<WorkerId> node : that) {
                        for (final WorkerId workerId : node) {
                            final UUIDId id = new UUIDId(BaseTransport.this);
                            ret.add(new RemoteExecutionImpl(new DistributedWorker(BaseTransport.this, workerId), id, id, new ChainImpl<Void>()));
                            ++i;
                        }
                    }
                    if (ret.isEmpty()) {
                        next.reject(new Exception("No remote workers found"));
                        return;
                    }
                }
                next.resolve(ret);
            }
        });
    }
}
