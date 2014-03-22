package io.machinecode.chainlink.core.deferred;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.THashSet;
import io.machinecode.chainlink.spi.deferred.ResolvedException;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.deferred.Listener;
import org.jboss.logging.Logger;

import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class DeferredImpl<T> implements Deferred<T> {

    private static final Logger log = Logger.getLogger(DeferredImpl.class);

    protected static final int PENDING   = 0;
    protected static final int RESOLVED  = 1;
    protected static final int REJECTED  = 2;
    protected static final int CANCELLED = 3;

    protected final Deferred<?>[] children;
    protected final Object lock = new Object();

    protected volatile int state = PENDING;

    protected volatile T value;
    protected volatile Throwable failure;

    private final Set<Listener> resolveListeners = new THashSet<Listener>(0);
    private final Set<Listener> rejectListeners = new THashSet<Listener>(0);
    private final Set<Listener> cancelListeners = new THashSet<Listener>(0);

    private final TIntObjectMap<Listener> chainListeners = new TIntObjectHashMap<Listener>(0);

    public DeferredImpl(final Deferred<?>... children) {
        this.children = children;
    }

    @Override
    public void resolve(final T value) {
        log().tracef(getResolveLogMessage());
        synchronized (lock) {
            this.value = value;
            if (this.state == PENDING) {
                this.state = RESOLVED;
            }
        }
        Throwable exception = null;
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
        if (exception != null) {
            reject(exception);
        } else {
            _notifyAll();
        }
    }

    @Override
    public void reject(final Throwable failure) {
        log().tracef(failure, getRejectLogMessage());
        synchronized (lock) {
            this.failure = failure;
            if (this.state != CANCELLED) {
                this.state = REJECTED;
            }
        }
        for (final Listener listener : rejectListeners) {
            try {
                listener.run(this);
            } catch (final Throwable e) {
                failure.addSuppressed(e);
            }
        }
        _notifyAll();
    }

    @Override
    public void always(final Listener listener) {
        boolean run = false;
        synchronized (lock) {
            switch(this.state) {
                case REJECTED:
                case CANCELLED:
                case RESOLVED:
                    run = true;
                case PENDING:
                default:
                    resolveListeners.add(listener);
                    rejectListeners.add(listener);
                    cancelListeners.add(listener);
            }
        }
        if (run) {
            listener.run(this);
        }
    }

    @Override
    public void onResolve(final Listener listener) {
        boolean run = false;
        synchronized (lock) {
            switch(this.state) {
                case REJECTED:
                case CANCELLED:
                    return;
                case RESOLVED:
                    run = true;
                case PENDING:
                default:
                    resolveListeners.add(listener);
            }
        }
        if (run) {
            listener.run(this);
        }
    }

    @Override
    public void onReject(final Listener listener) {
        boolean run = false;
        synchronized (lock) {
            switch(this.state) {
                case RESOLVED:
                case CANCELLED:
                    return;
                case REJECTED:
                    run = true;
                case PENDING:
                default:
                    rejectListeners.add(listener);
            }
        }
        if (run) {
            listener.run(this);
        }
    }

    @Override
    public void onCancel(final Listener listener) {
        boolean run = false;
        synchronized (lock) {
            switch(this.state) {
                case RESOLVED:
                case REJECTED:
                    return;
                case CANCELLED:
                    run = true;
                case PENDING:
                default:
                    cancelListeners.add(listener);
            }
        }
        if (run) {
            listener.run(this);
        }
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        log().tracef(getCancelLogMessage());
        final CancelListener listener = new CancelListener(mayInterruptIfRunning);
        traverse(listener);
        synchronized (lock) {
            if (listener.exception != null) {
                _notifyAll();
                throw listener.exception;
            }
            if (this.isCancelled()) {
                return true;
            }
            if (this.isResolved() || this.isRejected()) {
                return false;
            }
            this.state = CANCELLED;
        }
        RuntimeException exception = null;
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
            _notifyAll();
        }
    }

    @Override
    public boolean isDone() {
        synchronized (lock) {
            return this.isResolved() || this.isRejected() || this.isCancelled();
        }
    }

    @Override
    public boolean isCancelled() {
        synchronized (lock) {
            return this.state == CANCELLED;
        }
    }

    @Override
    public boolean isRejected() {
        synchronized (lock) {
            return this.state == REJECTED;
        }
    }

    @Override
    public boolean isResolved() {
        synchronized (lock) {
            return this.state == RESOLVED;
        }
    }

    @Override
    public T get() throws InterruptedException, ExecutionException, CancellationException {
        synchronized (lock) {
            await();
            switch (this.state) {
                case CANCELLED:
                    throw new CancellationException(Messages.format("CHAINLINK-004002.deferred.cancelled"));
                case REJECTED:
                    throw new ExecutionException(Messages.format("CHAINLINK-004001.deferred.rejected"), failure);
                case RESOLVED:
                    return value;
            }
            for (;;) {
                lock.wait();
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
        }
    }

    @Override
    public T get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException, CancellationException {
        synchronized (lock) {
            await(timeout, unit);
            switch (this.state) {
                case CANCELLED:
                    throw new CancellationException(Messages.format("CHAINLINK-004002.deferred.cancelled"));
                case REJECTED:
                    throw new ExecutionException(Messages.format("CHAINLINK-004001.deferred.rejected"), failure);
                case RESOLVED:
                    return value;
            }
            lock.wait(unit.toMillis(timeout));
            switch (this.state) {
                case CANCELLED:
                    throw new CancellationException(Messages.format("CHAINLINK-004002.deferred.cancelled"));
                case REJECTED:
                    throw new ExecutionException(Messages.format("CHAINLINK-004001.deferred.rejected"), failure);
                case RESOLVED:
                    return value;
            }
            throw new TimeoutException(getTimeoutExceptionMessage());
        }
    }

    @Override
    public Throwable getFailure() throws InterruptedException, ExecutionException {
        synchronized (lock) {
            await();
            switch (this.state) {
                case CANCELLED:
                    throw new CancellationException(Messages.format("CHAINLINK-004002.deferred.cancelled"));
                case RESOLVED:
                    throw new ResolvedException(Messages.format("CHAINLINK-004000.deferred.resolved"));
                case REJECTED:
                    return failure;
            }
            for (;;) {
                lock.wait();
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
        }
    }

    @Override
    public Throwable getFailure(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        await(timeout, unit);
        synchronized (lock) {
            switch (this.state) {
                case CANCELLED:
                    throw new CancellationException(Messages.format("CHAINLINK-004002.deferred.cancelled"));
                case RESOLVED:
                    throw new ResolvedException(Messages.format("CHAINLINK-004000.deferred.resolved"));
                case REJECTED:
                    return failure;
            }
            lock.wait(unit.toMillis(timeout));
            switch (this.state) {
                case CANCELLED:
                    throw new CancellationException(Messages.format("CHAINLINK-004002.deferred.cancelled"));
                case RESOLVED:
                    throw new ResolvedException(Messages.format("CHAINLINK-004000.deferred.resolved"));
                case REJECTED:
                    return failure;
            }
            throw new TimeoutException(getTimeoutExceptionMessage());
        }
    }

    @Override
    public void traverse(final Listener listener) {
        if (listener == null) {
            throw new IllegalArgumentException(); //TODO Message
        }
        RuntimeException exception = null;
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                Deferred<?> that = children[i];
                if (that == null) {
                    synchronized (chainListeners) {
                        chainListeners.put(i, listener);
                    }
                } else {
                    try {
                        listener.run(that);
                    } catch (final Throwable e) {
                        if (exception == null) {
                            exception = new RuntimeException(Messages.format("CHAINLINK-004005.deferred.get.exception"), e);
                        } else {
                            exception.addSuppressed(e);
                        }
                    }
                }
            }
        }
        if (exception != null) {
            log().warnf(exception, Messages.format("CHAINLINK-004005.deferred.get.exception"));
            throw exception;
        }
    }

    @Override
    public void await() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException(Messages.format("CHAINLINK-004004.deferred.interrupted"));
        }
        RuntimeException exception = null;
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                Deferred<?> that;
                synchronized (lock) {
                    that = children[i];
                    while (that == null) {
                        lock.wait();
                        that = children[i];
                    }
                }
                try {
                    that.await();
                } catch (final Throwable e) {
                    if (exception == null) {
                        exception = new RuntimeException(Messages.format("CHAINLINK-004005.deferred.get.exception"), e);
                    } else {
                        exception.addSuppressed(e);
                    }
                }
            }
        }
        if (exception != null) {
            log().warnf(exception, Messages.format("CHAINLINK-004005.deferred.get.exception"));
            throw exception;
        }
    }

    @Override
    public void await(final long timeout, final TimeUnit unit) throws InterruptedException, TimeoutException {
        if (Thread.interrupted()) {
            throw new InterruptedException(Messages.format("CHAINLINK-004004.deferred.interrupted"));
        }
        final long timeoutMillis = unit.toMillis(timeout);
        final long end = System.currentTimeMillis() + timeoutMillis;
        RuntimeException exception = null;
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                Deferred<?> that;
                synchronized (lock) {
                    that = children[i];
                    if (that == null) {
                        while (that == null) {
                            final long nextTimeout = _tryTimeout(end);
                            lock.wait(nextTimeout);
                            that = children[i];
                        }
                    }
                }
                try {
                    final long nextTimeout = _tryTimeout(end);
                    that.await(nextTimeout, MILLISECONDS);
                } catch (final Throwable e) {
                    if (exception == null) {
                        exception = new RuntimeException(Messages.format("CHAINLINK-004005.deferred.get.exception"), e);
                    } else {
                        exception.addSuppressed(e);
                    }
                }
            }
        }
        if (exception != null) {
            log().warnf(exception, Messages.format("CHAINLINK-004005.deferred.get.exception"));
            throw exception;
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

    protected void _notifyAll() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    protected Deferred<?> setChild(final int index, final Deferred<?> that) {
        children[index] = that;
        final Listener listener;
        synchronized (chainListeners) {
            listener = chainListeners.get(index);
        }
        if (listener == null) {
            return that;
        }
        listener.run(that);
        return that;
    }

    private long _tryTimeout(final long end) throws TimeoutException {
        final long nextTimeout = end - System.currentTimeMillis();
        if (nextTimeout <= 0) {
            throw new TimeoutException(getTimeoutExceptionMessage());
        }
        return nextTimeout;
    }

    private static final class CancelListener implements Listener {
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
