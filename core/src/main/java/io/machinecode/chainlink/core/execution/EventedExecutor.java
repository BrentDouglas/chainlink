package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.spi.configuration.ExecutorConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.then.api.Deferred;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.then.api.OnReject;
import io.machinecode.then.api.OnResolve;
import io.machinecode.then.api.Promise;
import io.machinecode.chainlink.core.then.AllChain;
import io.machinecode.chainlink.core.then.ChainImpl;
import io.machinecode.chainlink.core.then.RejectedChain;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class EventedExecutor implements Executor {

    private static final Logger log = Logger.getLogger(EventedExecutor.class);

    protected final Registry registry;
    private final ExecutorService cancellation = Executors.newCachedThreadPool();

    public EventedExecutor(final ExecutorConfiguration configuration) {
        this.registry = configuration.getRegistry();
    }

    @Override
    public void startup() {
        // no op
    }

    @Override
    public void shutdown() {
        this.cancellation.shutdown();
    }

    @Override
    public Chain<?> execute(final long jobExecutionId, final Executable executable) {
        final Chain<?> chain = new ChainImpl<Void>();
        final ChainId chainId = registry.registerJob(jobExecutionId, registry.generateChainId(), chain);
        _execute(executable, chainId);
        return chain;
    }

    @Override
    public Chain<?> execute(final Executable executable) {
        final Chain<?> chain = new ChainImpl<Void>();
        final ChainId chainId = registry.getJobRegistry(executable.getContext().getJobExecutionId())
                .registerChain(registry.generateChainId(), chain);
        _execute(executable, chainId);
        return chain;
    }

    private void _execute(final Executable executable, final ChainId chainId) {
        final WorkerId workerId = executable.getWorkerId();
        final Worker worker;
        if (workerId == null) {
            worker = registry.getWorker();
        } else {
            worker = registry.getWorker(workerId);
        }
        worker.execute(new ExecutableEventImpl(executable, chainId));
    }

    @Override
    public Chain<?> distribute(final int maxThreads, final Executable... executables) {
        final List<Worker> workers = registry.getWorkers(maxThreads);
        ListIterator<Worker> it = workers.listIterator();
        final Chain<?>[] chains = new Chain[executables.length];
        @SuppressWarnings("unchecked")
        final Promise<Worker.ChainAndId>[] promises = new Promise[executables.length];
        int i = 0;
        for (final Executable executable : executables) {
            if (!it.hasNext()) {
                it = workers.listIterator();
            }
            final Worker worker = it.next();
            final int index = i++;
            final Collect collect = new Collect() {
                @Override
                public void reject(final Throwable fail) {
                    log.errorf(fail, ""); //TODO Message
                    chains[index] = new RejectedChain<Void>(fail);
                }

                @Override
                public void resolve(final Worker.ChainAndId that) {
                    chains[index] = that.getChain();
                    registry.getJobRegistry(executable.getContext().getJobExecutionId())
                            .registerChain(that.getLocalId(), that.getChain());
                    worker.execute(new ExecutableEventImpl(executable, that.getRemoteId()));
                }
            };
            (promises[index] = worker.chain(executable))
                    .onResolve(collect)
                    .onReject(collect);
        }
        for (final Promise<Worker.ChainAndId> promise : promises) {
            try {
                promise.get();
            } catch (final Exception e) {
                throw new RuntimeException(e); //TODO Message?
            }
        }
        return new AllChain<Executable>(chains);
    }

    @Override
    public Chain<?> callback(final Executable executable, final ExecutionContext context) {
        final WorkerId workerId = executable.getWorkerId();
        final Worker worker;
        if (workerId == null) {
            worker = registry.getWorker();
        } else {
            worker = registry.getWorker(workerId);
        }
        try {
            return worker.chain(executable)
                    .onResolve(new OnResolve<Worker.ChainAndId>() {
                        @Override
                        public void resolve(final Worker.ChainAndId that) {
                            registry.getJobRegistry(executable.getContext().getJobExecutionId())
                                    .registerChain(that.getLocalId(), that.getChain());
                            worker.execute(new ExecutableEventImpl(executable, that.getRemoteId(), context));
                        }
                    }).get().getChain(); //TODO This is rubbish
        } catch (final Exception e) {
            throw new RuntimeException(e); //TODO
        }
    }

    @Override
    public Future<?> cancel(final Deferred<?> deferred) {
        return cancellation.submit(new Runnable() {
            @Override
            public void run() {
                deferred.cancel(true);
            }
        });
    }

    private interface Collect extends OnResolve<Worker.ChainAndId>, OnReject<Throwable> {}
}
