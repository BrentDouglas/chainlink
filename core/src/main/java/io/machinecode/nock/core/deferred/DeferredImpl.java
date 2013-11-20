package io.machinecode.nock.core.deferred;

import gnu.trove.set.hash.THashSet;
import io.machinecode.nock.spi.deferred.ResolvedException;
import io.machinecode.nock.spi.util.Messages;
import io.machinecode.nock.spi.deferred.Deferred;
import io.machinecode.nock.spi.deferred.Listener;
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
public class DeferredImpl<T, U extends Throwable> implements Deferred<T, U> {

    private static final Logger log = Logger.getLogger(DeferredImpl.class);

    protected static final int PENDING   = 0;
    protected static final int RESOLVED  = 1;
    protected static final int REJECTED  = 2;
    protected static final int CANCELLED = 3;

    protected final Deferred<?,?>[] chain;

    protected volatile int state = PENDING;

    protected volatile T value;
    protected volatile U failure;

    private final Set<Listener> resolveListeners = new THashSet<Listener>(0);
    private final Set<Listener> rejectListeners = new THashSet<Listener>(0);
    private final Set<Listener> cancelListeners = new THashSet<Listener>(0);

    public DeferredImpl(final Deferred<?,?>... chain) {
        this.chain = chain;
    }

    @Override
    public synchronized void resolve(final T that) {
        log.tracef(Messages.format("deferred.resolve"));
        this.state = RESOLVED;
        this.value = that;
        try {
            RuntimeException exception = null;
            for (final Listener listener : resolveListeners) {
                try {
                    listener.run(this);
                } catch (final RuntimeException e) {
                    if (exception == null) {
                        exception = e;
                    } else {
                        exception.addSuppressed(e);
                    }
                }
            }
            if (exception != null) {
                throw exception;
                //TODO Maybe this should call reject instead, requires signature change though
            }
        } finally {
            notifyAll();
        }
    }

    @Override
    public void reject(final U that) {
        log.tracef(Messages.format("deferred.reject"));
        this.state = REJECTED;
        this.failure = that;
        final Throwable throwable = that;
        try {
            for (final Listener listener : rejectListeners) {
                try {
                    listener.run(this);
                } catch (final RuntimeException e) {
                    throwable.addSuppressed(e);
                }
            }
        } finally {
            synchronized (this) {
                notifyAll();
            }
        }
    }

    @Override
    public synchronized void onResolve(final Listener listener) {
        switch(this.state) {
            case REJECTED:
            case CANCELLED:
                return;
            case RESOLVED:
                listener.run(this);
                return;
            case PENDING:
            default:
                resolveListeners.add(listener);
        }
    }

    @Override
    public synchronized void onReject(final Listener listener) {
        switch(this.state) {
            case RESOLVED:
            case CANCELLED:
                return;
            case REJECTED:
                listener.run(this);
                return;
            case PENDING:
            default:
                rejectListeners.add(listener);
        }
    }

    @Override
    public synchronized void onCancel(final Listener listener) {
        switch(this.state) {
            case RESOLVED:
            case REJECTED:
                return;
            case CANCELLED:
                listener.run(this);
                return;
            case PENDING:
            default:
                cancelListeners.add(listener);
        }
    }

    @Override
    public synchronized boolean cancel(final boolean mayInterruptIfRunning) {
        if (this.isCancelled()) {
            return true;
        }
        log.tracef(Messages.format("deferred.cancel"));
        RuntimeException exception = null;
        boolean cancelled = true;
        if (chain != null) {
            for (final Deferred<?,?> that : chain) {
                if (that == null) {
                    continue;
                }
                try {
                    cancelled = that.cancel(mayInterruptIfRunning) && cancelled;
                } catch (final RuntimeException e) {
                    if (exception == null) {
                        exception = e;
                    } else {
                        exception.addSuppressed(e);
                    }
                }
            }
        }
        if (exception != null) {
            notifyAll();
            throw exception;
        }
        if (this.isResolved() || this.isRejected()) {
            return false;
        }
        this.state = CANCELLED;
        try {
            for (final Listener listener : cancelListeners) {
                try {
                    listener.run(this);
                } catch (final RuntimeException e) {
                    if (exception == null) {
                        exception = e;
                    } else {
                        exception.addSuppressed(e);
                    }
                }
            }
            if (exception != null) {
                throw exception;
            }
            return cancelled;
        } finally {
            notifyAll();
        }
    }

    @Override
    public synchronized boolean isDone() {
        return this.isResolved() || this.isRejected() || this.isCancelled();
    }

    @Override
    public boolean isCancelled() {
        return this.state == CANCELLED;
    }

    @Override
    public boolean isRejected() {
        return this.state == REJECTED;
    }

    @Override
    public boolean isResolved() {
        return this.state == RESOLVED;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException, CancellationException {
        _checkState();
        synchronized (this) {
            switch (this.state) {
                case CANCELLED:
                    throw new CancellationException(Messages.format("deferred.cancelled"));
                case REJECTED:
                    throw new ExecutionException(Messages.format("deferred.rejected"), failure);
                case RESOLVED:
                    return value;
            }
        }
        for (;;) {
            synchronized (this) {
                wait();
            }
            switch (this.state) {
                case CANCELLED:
                    throw new CancellationException(Messages.format("deferred.cancelled"));
                case REJECTED:
                    throw new ExecutionException(Messages.format("deferred.rejected"), failure);
                case RESOLVED:
                    return value;
                //default/PENDING means this thread was notified before the computation actually completed
            }
        }
    }

    @Override
    public T get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException, CancellationException {
        _timedCheckState(timeout, unit);
        synchronized (this) {
            switch (this.state) {
                case CANCELLED:
                    throw new CancellationException(Messages.format("deferred.cancelled"));
                case REJECTED:
                    throw new ExecutionException(Messages.format("deferred.rejected"), failure);
                case RESOLVED:
                    return value;
            }
            wait(unit.toMillis(timeout));
        }
        switch (this.state) {
            case CANCELLED:
                throw new CancellationException(Messages.format("deferred.cancelled"));
            case REJECTED:
                throw new ExecutionException(Messages.format("deferred.rejected"), failure);
            case RESOLVED:
                return value;
        }
        throw new TimeoutException();
    }

    @Override
    public U getFailure() throws InterruptedException, ExecutionException {
        _checkState();
        synchronized (this) {
            switch (this.state) {
                case CANCELLED:
                    throw new CancellationException(Messages.format("deferred.cancelled"));
                case RESOLVED:
                    throw new ResolvedException(Messages.format("deferred.resolved"));
                case REJECTED:
                    return failure;
            }
        }
        for (;;) {
            synchronized (this) {
                wait();
            }
            switch (this.state) {
                case CANCELLED:
                    throw new CancellationException(Messages.format("deferred.cancelled"));
                case RESOLVED:
                    throw new ResolvedException(Messages.format("deferred.resolved"));
                case REJECTED:
                    return failure;
                //default/PENDING means this thread was notified before the computation actually completed
            }
        }
    }

    @Override
    public U getFailure(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        _timedCheckState(timeout, unit);
        synchronized (this) {
            switch (this.state) {
                case CANCELLED:
                    throw new CancellationException(Messages.format("deferred.cancelled"));
                case RESOLVED:
                    throw new ResolvedException(Messages.format("deferred.resolved"));
                case REJECTED:
                    return failure;
            }
            wait(unit.toMillis(timeout));
        }
        switch (this.state) {
            case CANCELLED:
                throw new CancellationException(Messages.format("deferred.cancelled"));
            case RESOLVED:
                throw new ResolvedException(Messages.format("deferred.resolved"));
            case REJECTED:
                return failure;
        }
        throw new TimeoutException(Messages.format("deferred.timeout"));
    }

    private void _checkState() throws InterruptedException, ExecutionException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        RuntimeException exception = null;
        if (chain != null) {
            for (final Deferred<?,?> that : chain) {
                if (that == null) {
                    continue;
                }
                if (that.isCancelled()) {
                    continue;
                }
                try {
                    that.get();
                } catch (final CancellationException e) {
                    // Ignore these
                } catch (final RuntimeException e) {
                    if (exception == null) {
                        exception = e;
                    } else {
                        exception.addSuppressed(e);
                    }
                }
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

    private void _timedCheckState(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        final long timeoutMillis = unit.toMillis(timeout);
        final long end = System.currentTimeMillis() + timeoutMillis;
        RuntimeException exception = null;
        if (chain != null) {
            for (final Deferred<?,?> that : chain) {
                if (that == null) {
                    continue;
                }
                if (that.isCancelled()) {
                    continue;
                }
                try {
                    final long nextTimeout = end - System.currentTimeMillis();
                    if (nextTimeout <= 0) {
                        throw new TimeoutException();
                    }
                    that.get(nextTimeout, MILLISECONDS);
                } catch (final CancellationException e) {
                    // Ignore these
                } catch (final RuntimeException e) {
                    if (exception == null) {
                        exception = e;
                    } else {
                        exception.addSuppressed(e);
                    }
                }
            }
        }
        if (exception != null) {
            throw exception;
        }
    }
}
