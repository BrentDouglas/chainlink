package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.core.deferred.AllDeferred;
import io.machinecode.chainlink.core.deferred.LinkedDeferred;
import io.machinecode.chainlink.spi.configuration.ExecutorConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.transport.DeferredId;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.chainlink.spi.transport.WorkerId;
import io.machinecode.chainlink.spi.util.Pair;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.ListIterator;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class EventedExecutor implements Executor {

    private static final Logger log = Logger.getLogger(EventedExecutor.class);

    protected final Transport transport;
    private final ExecutorService cancellation = Executors.newCachedThreadPool();

    public EventedExecutor(final ExecutorConfiguration configuration) {
        this.transport = configuration.getTransport();
    }


    @Override
    public void startup() {
        //
    }

    @Override
    public void shutdown() {
        this.cancellation.shutdown();
    }

    @Override
    public Deferred<?> execute(final long jobExecutionId, final Executable executable) {
        final Deferred<?> deferred = new LinkedDeferred<Void>();
        final DeferredId deferredId = new UUIDDeferredId(UUID.randomUUID());
        transport.registerDeferred(jobExecutionId, deferredId, deferred);
        transport.registerJob(jobExecutionId, deferred);
        _execute(executable, deferredId);
        return deferred;
    }

    @Override
    public Deferred<?> execute(final Executable executable) {
        final Deferred<?> deferred = new LinkedDeferred<Void>();
        final DeferredId deferredId = new UUIDDeferredId(UUID.randomUUID());
        transport.registerDeferred(executable.getContext().getJobExecutionId(), deferredId, deferred);
        _execute(executable, deferredId);
        return deferred;
    }

    private void _execute(final Executable executable, final DeferredId deferredId) {
        final WorkerId workerId = executable.getWorkerId();
        final Worker worker;
        if (workerId == null) {
            worker = transport.getWorker();
        } else {
            worker = transport.getWorker(workerId);
        }
        worker.addExecutable(new ExecutableEventImpl(executable, deferredId));
    }

    @Override
    public Deferred<?> distribute(final int maxThreads, final Executable... executables) {
        final List<Worker> workers = transport.getWorkers(maxThreads);
        ListIterator<Worker> it = workers.listIterator();
        final Deferred<?>[] deferreds = new Deferred[executables.length];
        int i = 0;
        for (final Executable executable : executables) {
            if (!it.hasNext()) {
                it = workers.listIterator();
            }
            final Worker worker = it.next();
            final Pair<DeferredId, Deferred<?>> pair = worker.createDistributedDeferred(executable);
            deferreds[i++] = pair.getValue();
            transport.registerDeferred(executable.getContext().getJobExecutionId(), pair.getName(), pair.getValue());
            worker.addExecutable(new ExecutableEventImpl(executable, pair.getName()));
        }
        return new AllDeferred<Executable>(deferreds);
    }

    @Override
    public Deferred<?> callback(final Executable executable, final ExecutionContext context) {
        final WorkerId workerId = executable.getWorkerId();
        Worker worker;
        if (workerId == null) {
            worker = transport.getWorker();
        } else {
            worker = transport.getWorker(workerId);
        }
        final Deferred<?> deferred = new LinkedDeferred<Void>();
        final DeferredId deferredId = new UUIDDeferredId(UUID.randomUUID());
        transport.registerDeferred(executable.getContext().getJobExecutionId(), deferredId, deferred);
        worker.addExecutable(new ExecutableEventImpl(executable, deferredId, context));
        return deferred;
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
}
