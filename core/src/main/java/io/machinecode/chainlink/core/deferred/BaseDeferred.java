package io.machinecode.chainlink.core.deferred;

import gnu.trove.set.hash.THashSet;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.deferred.Listener;
import io.machinecode.chainlink.spi.deferred.ResolvedException;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.util.Pair;
import org.jboss.logging.Logger;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class BaseDeferred<T> implements Deferred<T> {

    private static final Logger log = Logger.getLogger(BaseDeferred.class);

    protected static final int PENDING = 0;
    protected static final int RESOLVED = 1;
    protected static final int REJECTED = 2;
    protected static final int CANCELLED = 3;

    protected final Lock lock = new ReentrantLock();
    protected final Condition condition = lock.newCondition();
    protected final Queue<Pair<Lock, Condition>> waiting = new LinkedList<Pair<Lock, Condition>>();

    protected volatile int state = PENDING;

    protected volatile T value;
    protected volatile Throwable failure;

    protected final Set<Listener> resolveListeners = new THashSet<Listener>(0);
    protected final Set<Listener> rejectListeners = new THashSet<Listener>(0);
    protected final Set<Listener> cancelListeners = new THashSet<Listener>(0);
    protected final Set<Listener> linkListeners = new THashSet<Listener>(0);

    protected final AtomicBoolean listenerLock = new AtomicBoolean(false);

    @Override
    public void resolve(final T value) {
        log().tracef(getResolveLogMessage());
        lock.lock();
        try {
            this.value = value;
            if (this.state == PENDING) {
                this.state = RESOLVED;
            }
        } finally {
            lock.unlock();
        }
        Throwable exception = null;
        while (!listenerLock.compareAndSet(false, true)) {}
        try {
            for (final Listener listener : resolveListeners) {
                try {
                    listener.run(this);
                } catch (final Throwable e) {
                    if (exception == null) {
                        exception = e;
                    } else {
                        exception.addSuppressed(e);
                    }
                }
            }
        } finally {
            listenerLock.set(false);
        }
        if (exception != null) {
            reject(exception);
        } else {
            signal();
        }
    }

    @Override
    public void reject(final Throwable failure) {
        log().tracef(failure, getRejectLogMessage());
        lock.lock();
        try {
            this.failure = failure;
            if (this.state != CANCELLED) {
                this.state = REJECTED;
            }
        } finally {
            lock.unlock();
        }
        while (!listenerLock.compareAndSet(false, true)) {}
        try {
            for (final Listener listener : rejectListeners) {
                try {
                    listener.run(this);
                } catch (final Throwable e) {
                    failure.addSuppressed(e);
                }
            }
        } finally {
            listenerLock.set(false);
        }
        signal();
    }

    @Override
    public void always(final Listener listener) {
        boolean run = false;
        lock.lock();
        try {
            switch (this.state) {
                case REJECTED:
                case CANCELLED:
                case RESOLVED:
                    run = true;
                case PENDING:
                default:
                    while (!listenerLock.compareAndSet(false, true)) {}
                    try {
                        resolveListeners.add(listener);
                        rejectListeners.add(listener);
                        cancelListeners.add(listener);
                    } finally {
                        listenerLock.set(false);
                    }
            }
        } finally {
            lock.unlock();
        }
        if (run) {
            listener.run(this);
        }
    }

    @Override
    public void onResolve(final Listener listener) {
        boolean run = false;
        lock.lock();
        try {
            switch (this.state) {
                case REJECTED:
                case CANCELLED:
                    return;
                case RESOLVED:
                    run = true;
                case PENDING:
                default:
                    while (!listenerLock.compareAndSet(false, true)) {}
                    try {
                        resolveListeners.add(listener);
                    } finally {
                        listenerLock.set(false);
                    }
            }
        } finally {
            lock.unlock();
        }
        if (run) {
            listener.run(this);
        }
    }

    @Override
    public void onReject(final Listener listener) {
        boolean run = false;
        lock.lock();
        try {
            switch (this.state) {
                case RESOLVED:
                case CANCELLED:
                    return;
                case REJECTED:
                    run = true;
                case PENDING:
                default:
                    while (!listenerLock.compareAndSet(false, true)) {}
                    try {
                        rejectListeners.add(listener);
                    } finally {
                        listenerLock.set(false);
                    }
            }
        } finally {
            lock.unlock();
        }
        if (run) {
            listener.run(this);
        }
    }

    @Override
    public void onCancel(final Listener listener) {
        boolean run = false;
        lock.lock();
        try {
            switch (this.state) {
                case RESOLVED:
                case REJECTED:
                    return;
                case CANCELLED:
                    run = true;
                case PENDING:
                default:
                    while (!listenerLock.compareAndSet(false, true)) {}
                    try {
                        cancelListeners.add(listener);
                    } finally {
                        listenerLock.set(false);
                    }
            }
        } finally {
            lock.unlock();
        }
        if (run) {
            listener.run(this);
        }
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        log().tracef(getCancelLogMessage());
        final CancelListener listener = new CancelListener(mayInterruptIfRunning);
        lock.lock();
        try {
            onLink(listener);
            if (listener.exception != null) {
                signal();
                throw listener.exception;
            }
            if (this.isCancelled()) {
                return true;
            }
            if (this.isResolved() || this.isRejected()) {
                return false;
            }
            this.state = CANCELLED;
        } finally {
            lock.unlock();
        }
        RuntimeException exception = null;
        while (!listenerLock.compareAndSet(false, true)) {}
        try {
            for (final Listener cancelListener : cancelListeners) {
                try {
                    cancelListener.run(this);
                } catch (final Throwable e) {
                    if (exception == null) {
                        exception = new RuntimeException(Messages.format("CHAINLINK-004006.deferred.cancel.exception"), e);
                    } else {
                        exception.addSuppressed(e);
                    }
                }
            }
            if (exception != null) {
                throw exception;
            }
            return listener.cancelled;
        } finally {
            listenerLock.set(false);
            signal();
        }
    }

    @Override
    public boolean isDone() {
        lock.lock();
        try {
            return this.isResolved() || this.isRejected() || this.isCancelled();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isCancelled() {
        lock.lock();
        try {
            return this.state == CANCELLED;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isRejected() {
        lock.lock();
        try {
            return this.state == REJECTED;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isResolved() {
        lock.lock();
        try {
            return this.state == RESOLVED;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T get() throws InterruptedException, ExecutionException, CancellationException {
        lock.lock();
        try {
            await(lock, condition);
            switch (this.state) {
                case CANCELLED:
                    throw new CancellationException(Messages.format("CHAINLINK-004002.deferred.cancelled"));
                case REJECTED:
                    throw new ExecutionException(Messages.format("CHAINLINK-004001.deferred.rejected"), failure);
                case RESOLVED:
                    return value;
            }
            for (;;) {
                condition.await();
                switch (this.state) {
                    case CANCELLED:
                        throw new CancellationException(Messages.format("CHAINLINK-004002.deferred.cancelled"));
                    case REJECTED:
                        throw new ExecutionException(Messages.format("CHAINLINK-004001.deferred.rejected"), failure);
                    case RESOLVED:
                        return value;
                    //default/PENDING means this thread was notified before the computation actually completed
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException, CancellationException {
        lock.lock();
        try {
            await(timeout, unit, lock, condition);
            switch (this.state) {
                case CANCELLED:
                    throw new CancellationException(Messages.format("CHAINLINK-004002.deferred.cancelled"));
                case REJECTED:
                    throw new ExecutionException(Messages.format("CHAINLINK-004001.deferred.rejected"), failure);
                case RESOLVED:
                    return value;
            }
            condition.await(timeout, unit);
            switch (this.state) {
                case CANCELLED:
                    throw new CancellationException(Messages.format("CHAINLINK-004002.deferred.cancelled"));
                case REJECTED:
                    throw new ExecutionException(Messages.format("CHAINLINK-004001.deferred.rejected"), failure);
                case RESOLVED:
                    return value;
            }
            throw new TimeoutException(getTimeoutExceptionMessage());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Throwable getFailure() throws InterruptedException, ExecutionException {
        lock.lock();
        try {
            condition.await();
            switch (this.state) {
                case CANCELLED:
                    throw new CancellationException(Messages.format("CHAINLINK-004002.deferred.cancelled"));
                case RESOLVED:
                    throw new ResolvedException(Messages.format("CHAINLINK-004000.deferred.resolved"));
                case REJECTED:
                    return failure;
            }
            for (;;) {
                condition.await();
                switch (this.state) {
                    case CANCELLED:
                        throw new CancellationException(Messages.format("CHAINLINK-004002.deferred.cancelled"));
                    case RESOLVED:
                        throw new ResolvedException(Messages.format("CHAINLINK-004000.deferred.resolved"));
                    case REJECTED:
                        return failure;
                    //default/PENDING means this thread was notified before the computation actually completed
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Throwable getFailure(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        await(timeout, unit, lock, condition);
        lock.lock();
        try {
            switch (this.state) {
                case CANCELLED:
                    throw new CancellationException(Messages.format("CHAINLINK-004002.deferred.cancelled"));
                case RESOLVED:
                    throw new ResolvedException(Messages.format("CHAINLINK-004000.deferred.resolved"));
                case REJECTED:
                    return failure;
            }
            condition.await(timeout, unit);
            switch (this.state) {
                case CANCELLED:
                    throw new CancellationException(Messages.format("CHAINLINK-004002.deferred.cancelled"));
                case RESOLVED:
                    throw new ResolvedException(Messages.format("CHAINLINK-004000.deferred.resolved"));
                case REJECTED:
                    return failure;
            }
            throw new TimeoutException(getTimeoutExceptionMessage());
        } finally {
            lock.unlock();
        }
    }

    protected String getResolveLogMessage() {
        return Messages.get("CHAINLINK-004100.deferred.resolve");
    }

    protected String getRejectLogMessage() {
        return Messages.get("CHAINLINK-004101.deferred.reject");
    }

    protected String getCancelLogMessage() {
        return Messages.get("CHAINLINK-004102.deferred.cancel");
    }

    protected String getTimeoutExceptionMessage() {
        return Messages.get("CHAINLINK-004003.deferred.timeout");
    }

    protected Logger log() {
        return log;
    }

    @Override
    public void signal() {
        lock.lock();
        try {
            Pair<Lock, Condition> that;
            while ((that = waiting.poll()) != null) {
                that.getName().lock();
                try {
                    that.getValue().signalAll();
                } finally {
                    that.getName().unlock();
                }
            }
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    protected long _tryTimeout(final long end) throws TimeoutException {
        final long nextTimeout = end - System.currentTimeMillis();
        if (nextTimeout <= 0) {
            throw new TimeoutException(getTimeoutExceptionMessage());
        }
        return nextTimeout;
    }

    private static final class CancelListener implements Listener, Serializable {
        private final boolean mayInterruptIfRunning;
        private boolean cancelled = true;
        private RuntimeException exception = null;

        private CancelListener(final boolean mayInterruptIfRunning) {
            this.mayInterruptIfRunning = mayInterruptIfRunning;
        }

        @Override
        public void run(final Deferred<?> that) {
            try {
                cancelled = that.cancel(mayInterruptIfRunning) && cancelled;
            } catch (final Throwable e) {
                if (exception == null) {
                    exception = new RuntimeException(Messages.format("CHAINLINK-004006.deferred.cancel.exception"), e);
                } else {
                    exception.addSuppressed(e);
                }
            }
        }
    }
}
