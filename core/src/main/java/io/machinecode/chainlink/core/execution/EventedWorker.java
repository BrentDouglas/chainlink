package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.core.registry.UUIDId;
import io.machinecode.chainlink.core.then.ChainImpl;
import io.machinecode.chainlink.core.then.Notify;
import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.ResolvedDeferred;
import org.jboss.logging.Logger;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
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
        this.workerId = configuration.getRegistry().generateWorkerId(this);
    }

    @Override
    public WorkerId id() {
        return workerId;
    }

    @Override
    public void execute(final ExecutableEvent event) {
        final Executable executable = event.getExecutable();
        log.debugf(Messages.get("CHAINLINK-024005.worker.add.executable"), executable.getContext(), this, executable);
        synchronized (lock) {
            executables.add(event);
            lock.notifyAll();
        }
    }

    @Override
    public Promise<ChainAndId,Throwable,?> chain(final Executable executable) {
        final UUIDId id = new UUIDId();
        return new ResolvedDeferred<ChainAndId,Throwable,Void>(new ChainAndId(id, id, new ChainImpl<Void>()));
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
        final ExecutionContext context = executable.getContext();
        final ExecutionContext previous = event.getContext();
        try {
            final Chain<?> chain = configuration.getRegistry()
                    .getChain(context.getJobExecutionId(), event.getChainId());
            chain.onComplete(notify);
            executable.execute(configuration, chain, workerId, previous);
        } catch (final Throwable e) {
            log.errorf(e, Messages.get("CHAINLINK-024004.worker.execute.execution"), context, this, executable);
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
    public void close() {
        running = false;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[workerId=" + workerId + ",running=" + running + ",queued=" + executables.size() + "]";
    }
}
