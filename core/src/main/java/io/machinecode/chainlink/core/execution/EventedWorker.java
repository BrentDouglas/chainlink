package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.core.deferred.LinkedDeferred;
import io.machinecode.chainlink.core.deferred.Notify;
import io.machinecode.chainlink.jsl.core.util.ImmutablePair;
import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.transport.DeferredId;
import io.machinecode.chainlink.spi.transport.WorkerId;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.util.Pair;
import org.jboss.logging.Logger;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class EventedWorker extends Thread implements Worker {

    private static final AtomicLong IDS = new AtomicLong();

    private static final Logger log = Logger.getLogger(EventedWorker.class);

    protected final WorkerId workerId;
    protected final Object lock = new Object();
    protected final Queue<ExecutableEvent> executables = new LinkedList<ExecutableEvent>();
    protected final Notify notify = new Notify(lock);
    protected final RuntimeConfiguration configuration;
    protected volatile boolean running = true;

    public EventedWorker(final RuntimeConfiguration configuration) {
        super("Chainlink worker - " + IDS.incrementAndGet());
        this.configuration = configuration;
        this.workerId = configuration.getTransport().generateWorkerId(this);
    }

    @Override
    public WorkerId getWorkerId() {
        return workerId;
    }

    @Override
    public void addExecutable(final ExecutableEvent event) {
        final Executable executable = event.getExecutable();
        log.debugf(Messages.get("CHAINLINK-024005.worker.add.executable"), this, executable);
        synchronized (lock) {
            executables.add(event);
            lock.notifyAll();
        }
    }

    @Override
    public Pair<DeferredId, Deferred<?>> createDistributedDeferred(final Executable executable) {
        return ImmutablePair.<DeferredId, Deferred<?>>of(new UUIDDeferredId(UUID.randomUUID()), new LinkedDeferred<Void>());
    }

    @Override
    public void run() {
        while (running) {
            _runExecutable();
            _awaitIfEmpty();
        }
    }

    private void _runExecutable() {
        final ExecutableEvent event = _nextFromQueue();
        if (event == null) {
            return;
        }
        final Executable executable = event.getExecutable();
        final Deferred<?> deferred = configuration.getTransport().getDeferred(
                executable.getContext().getJobExecutionId(),
                event.getDeferredId()
        );
        try {
            deferred.always(notify);
            executable.execute(configuration, deferred, workerId, event.getContext());
        } catch (final Throwable e) {
            log.errorf(e, Messages.get("CHAINLINK-024004.worker.execute.execution"), this, executable);
        }
    }

    private void _awaitIfEmpty() {
        try {
            synchronized (lock) {
                if (executables.isEmpty()) {
                    log.tracef(Messages.get("CHAINLINK-024002.worker.waiting"), this);
                    lock.wait();
                    log.tracef(Messages.get("CHAINLINK-024003.worker.awake"), this);
                }
            }
        } catch (final InterruptedException ie) {
            log.infof(Messages.get("CHAINLINK-024001.worker.interrupted"), this);
            shutdown();
        }
    }

    private ExecutableEvent _nextFromQueue() {
        synchronized (lock) {
            try {
                return executables.remove();
            } catch (final NoSuchElementException e) {
                return null;
            }
        }
    }

    @Override
    public synchronized void startup() {
        super.start();
    }

    @Override
    public void shutdown() {
        running = false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[workerId=" + workerId + ",running=" + running + ",queued=" + executables.size() + "]";
    }
}
