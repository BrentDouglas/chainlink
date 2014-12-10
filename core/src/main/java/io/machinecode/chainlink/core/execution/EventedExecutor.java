package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.core.configuration.RuntimeConfigurationImpl;
import io.machinecode.chainlink.core.then.AllChain;
import io.machinecode.chainlink.core.then.ChainImpl;
import io.machinecode.chainlink.core.then.RejectedChain;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.ChainAndIds;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.then.api.OnReject;
import io.machinecode.then.api.OnResolve;
import io.machinecode.then.api.Promise;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class EventedExecutor implements Executor {

    private static final Logger log = Logger.getLogger(EventedExecutor.class);

    protected final RuntimeConfiguration configuration;
    protected final Registry registry;
    protected final Transport<?> transport;
    private final ExecutorService cancellation = Executors.newSingleThreadExecutor();

    public EventedExecutor(final Dependencies dependencies, final Properties properties) {
        this.configuration = new RuntimeConfigurationImpl(this,
                dependencies.getRegistry(),
                dependencies.getTransport(),
                dependencies.getTransactionManager(),
                dependencies.getInjectionContext()
        );
        this.registry = dependencies.getRegistry();
        this.transport = configuration.getTransport();
        final int numThreads;
        try {
            numThreads = Integer.parseInt(properties.getProperty(Constants.THREAD_POOL_SIZE, Constants.Defaults.THREAD_POOL_SIZE));
        } catch (final NumberFormatException e) {
            throw new RuntimeException(e); //TODO Message
        }
        for (int i = 0; i < numThreads; ++i) {
            final Worker worker;
            try {
                worker = new EventedWorker(this.configuration);
            } catch (final Exception e) {
                throw new RuntimeException(e); //TODO Message
            }
            worker.start();
            this.transport.registerWorker(worker.id(), worker);
        }
    }

    @Override
    public void close() {
        this.cancellation.shutdown();
    }

    @Override
    public Chain<?> execute(final long jobExecutionId, final Executable executable) {
        final Chain<?> chain = new ChainImpl<Void>();
        final ChainId chainId = transport.generateChainId();
        registry.registerJob(jobExecutionId, chainId, chain);
        _execute(executable, chainId);
        return chain;
    }

    @Override
    public Chain<?> execute(final Executable executable) {
        final Chain<?> chain = new ChainImpl<Void>();
        final ChainId chainId = transport.generateChainId();
        registry.registerChain(executable.getContext().getJobExecutionId(), chainId, chain);
        _execute(executable, chainId);
        return chain;
    }

    private void _execute(final Executable executable, final ChainId chainId) {
        final WorkerId workerId = executable.getWorkerId();
        final Worker worker;
        if (workerId == null) {
            worker = transport.getWorker();
        } else {
            worker = transport.getWorker(workerId);
        }
        worker.execute(new ExecutableEventImpl(executable, chainId));
    }

    @Override
    public Chain<?> distribute(final int maxThreads, final Executable... executables) {
        final List<Worker> workers = transport.getWorkers(maxThreads);
        ListIterator<Worker> it = workers.listIterator();
        final Chain<?>[] chains = new Chain[executables.length];
        @SuppressWarnings("unchecked")
        final Promise<ChainAndIds,Throwable,?>[] promises = new Promise[executables.length];
        int i = 0;
        for (final Executable executable : executables) {
            if (!it.hasNext()) {
                it = workers.listIterator();
            }
            final Worker worker = it.next();
            final int index = i++;
            final long jobExecutionId = executable.getContext().getJobExecutionId();
            final Collect collect = new Collect() {
                @Override
                public void reject(final Throwable fail) {
                    log.errorf(fail, ""); //TODO Message
                    chains[index] = new RejectedChain<Void>(fail);
                }

                @Override
                public void resolve(final ChainAndIds that) {
                    chains[index] = that.getChain();
                    registry.registerChain(jobExecutionId, that.getLocalId(), that.getChain());
                    worker.execute(new ExecutableEventImpl(executable, that.getRemoteId()));
                }
            };
            (promises[index] = worker.chain(jobExecutionId))
                    .onResolve(collect)
                    .onReject(collect);
        }
        for (final Promise<ChainAndIds,Throwable,?> promise : promises) {
            try {
                promise.get();
            } catch (final Exception e) {
                throw new RuntimeException(e); //TODO Message?
            }
        }
        return new AllChain<Executable>(chains);
    }

    @Override
    public Chain<?> callback(final ExecutableId executableId, final ExecutionContext context) {
        final long jobExecutionId = context.getJobExecutionId();
        final Worker worker = transport.getWorker(jobExecutionId, executableId);
        try {
            return worker.chain(jobExecutionId)
                    .onResolve(new OnResolve<ChainAndIds>() {
                        @Override
                        public void resolve(final ChainAndIds that) {
                            registry.registerChain(jobExecutionId, that.getLocalId(), that.getChain());
                            worker.callback(new CallbackEventImpl(jobExecutionId, executableId, that.getRemoteId(), context));
                        }
                    })
                    .get()
                    .getChain(); //TODO This is rubbish
        } catch (final Exception e) {
            throw new RuntimeException(e); //TODO
        }
    }

    @Override
    public Future<?> cancel(final Future<?> promise) {
        return cancellation.submit(new Runnable() {
            @Override
            public void run() {
                promise.cancel(true);
            }
        });
    }

    private interface Collect extends OnResolve<ChainAndIds>, OnReject<Throwable> {}
}
