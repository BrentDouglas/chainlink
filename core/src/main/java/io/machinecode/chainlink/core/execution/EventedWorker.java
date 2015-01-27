package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.core.registry.LocalRegistry;
import io.machinecode.chainlink.core.registry.UUIDId;
import io.machinecode.chainlink.core.then.Notify;
import io.machinecode.chainlink.core.transport.WorkerState;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.CallbackEvent;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.execution.WorkerId;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.then.Chain;
import org.jboss.logging.Logger;

import java.util.LinkedList;
import java.util.Queue;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class EventedWorker implements Worker, WorkerState, Runnable, AutoCloseable {

    private static final Logger log = Logger.getLogger(EventedWorker.class);

    protected final WorkerId workerId;
    protected final Object lock;
    protected final Queue<ExecutableEvent> executables = new LinkedList<>();
    protected final Queue<CallbackEvent> callbacks = new LinkedList<>();
    protected final Notify notify;
    protected final Configuration configuration;
    protected volatile boolean started = true;
    protected volatile boolean stopped = false;

    public EventedWorker(final Configuration configuration) {
        this.configuration = configuration;
        this.workerId = new UUIDId(configuration.getTransport());
        lock = new Object() {
            @Override
            public String toString() {
                return "lock-" + workerId;
            }
        };
        notify = new Notify(lock);
    }

    @Override
    public WorkerId getId() {
        return workerId;
    }

    public boolean isActive() {
        return started && !stopped;
    }

    @Override
    public void execute(final ExecutableEvent event) {
        final Executable executable = event.getExecutable();
        synchronized (lock) {
            log.debugf(Messages.get("CHAINLINK-024005.worker.add.executable"), executable.getContext(), this, event);
            executables.add(event);
            lock.notifyAll();
        }
    }

    @Override
    public void callback(final CallbackEvent event) {
        synchronized (lock) {
            log.debugf(Messages.get("CHAINLINK-024006.worker.add.callback"), event.getContext(), this, event);
            callbacks.add(event);
            lock.notifyAll();
        }
    }

    @Override
    public void run() {
        this.started = true;
        boolean ran;
        do {
            _awaitIfEmpty();
            ran = _runExecutable();
        } while (!stopped || ran);
    }

    private boolean _runExecutable() {
        final ExecutionContext previous;
        final Executable executable;
        final ChainId chainId;
        final CallbackEvent cb;
        final ExecutableEvent ex;
        synchronized (lock) {
            cb = _nextCallback();
            if (cb == null) {
                ex = _nextExecutable();
            } else {
                ex = null;
            }
        }
        if (cb == null) {
            try {
                if (ex == null) {
                    return false;
                }
                executable = ex.getExecutable();
                previous = null;
                chainId = ex.getChainId();
            } catch (final Throwable e) {
                log.errorf(e, Messages.get("CHAINLINK-024007.worker.fetch.exception"), this, ex);
                return false;
            }
        } else {
            try {
                executable = configuration.getRegistry()
                        .getExecutable(cb.getJobExecutionId(), cb.getExecutableId());
                LocalRegistry.assertExecutable(executable, cb.getJobExecutionId(), cb.getExecutableId());
                previous = cb.getContext();
                chainId = cb.getChainId();
            } catch (final Throwable e) {
                log.errorf(e, Messages.get("CHAINLINK-024007.worker.fetch.exception"), this, cb);
                return false;
            }
        }
        final ExecutionContext context = executable.getContext();
        try {
            final Chain<?> chain = configuration.getRegistry()
                    .getChain(context.getJobExecutionId(), chainId);
            LocalRegistry.assertChain(chain, context.getJobExecutionId(), chainId);
            chain.onComplete(notify);
            executable.execute(configuration, chain, workerId, previous);
        } catch (final Throwable e) {
            log.errorf(e, Messages.get("CHAINLINK-024004.worker.execute.exception"), context, this, executable);
        }
        return true;
    }

    private void _awaitIfEmpty() {
        try {
            synchronized (lock) {
                if (executables.isEmpty() && callbacks.isEmpty()) {
                    log.tracef(Messages.get("CHAINLINK-024002.worker.waiting"), this);
                    lock.wait();
                    log.tracef(Messages.get("CHAINLINK-024003.worker.awake"), this);
                }
            }
        } catch (final InterruptedException ie) {
            log.infof(Messages.get("CHAINLINK-024001.worker.interrupted"), this);
        }
    }

    private ExecutableEvent _nextExecutable() {
        return executables.poll();
    }

    private CallbackEvent _nextCallback() {
        return callbacks.poll();
    }

    @Override
    public void close() {
        stopped = true;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[workerId=" + workerId + ",executables=" + executables.size() + ",callbacks=" + callbacks.size() + ",running=" + isActive() + "]";
    }

    @Override
    public int getExecutions() {
        return executables.size();
    }

    @Override
    public int getCallbacks() {
        return callbacks.size();
    }
}
