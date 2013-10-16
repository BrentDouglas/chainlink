package io.machinecode.nock.core.work;

import io.machinecode.nock.spi.work.Deferred;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class DeferredImpl<T> implements Deferred<T> {

    private final Deferred[] chain;

    private volatile boolean cancelled = false;
    private volatile boolean done = false;
    private volatile T value;

    public DeferredImpl(final Deferred... chain) {
        this.chain = chain;
    }

    @Override
    public synchronized void resolve(final T that) {
        this.done = true;
        this.value = that;
        notifyAll();
    }

    @Override
    public synchronized boolean cancel(final boolean mayInterruptIfRunning) {
        if (this.cancelled) {
            return true;
        }
        this.cancelled = true;
        try {
            boolean cancelled = true;
            RuntimeException exception = null;
            if (chain != null) {
                for (final Deferred that : chain) {
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
                throw exception;
            }
            return cancelled;
        } finally {
            notifyAll();
        }
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isDone() {
        return done || cancelled;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException, CancellationException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        synchronized (this) {
            if (cancelled) {
                throw new CancellationException();
            }
            if (!done) {
                wait();
            }
        }
        if (cancelled) {
            throw new CancellationException();
        }
        return value;
    }

    @Override
    public T get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException, CancellationException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        synchronized (this) {
            if (cancelled) {
                throw new CancellationException();
            }
            if (!done) {
                wait(unit.toMillis(timeout));
            }
        }
        if (cancelled) {
            throw new CancellationException();
        }
        if (!done) {
            throw new TimeoutException();
        }
        return value;
    }
}
