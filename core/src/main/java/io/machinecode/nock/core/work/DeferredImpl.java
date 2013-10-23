package io.machinecode.nock.core.work;

import gnu.trove.set.hash.THashSet;
import io.machinecode.nock.spi.work.Deferred;
import io.machinecode.nock.spi.work.Listener;

import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class DeferredImpl<T> implements Deferred<T> {

    protected final Deferred<?>[] chain;

    protected volatile boolean cancelled = false;
    protected volatile boolean done = false;
    protected volatile T value;

    protected final Set<Runnable> listeners = new THashSet<Runnable>();

    public DeferredImpl(final Deferred<?>... chain) {
        this.chain = chain;
    }

    @Override
    public synchronized void resolve(final T that) {
        this.done = true;
        this.value = that;
        for (final Runnable listener : listeners) {
            listener.run();
        }
        notifyAll();
    }

    @Override
    public synchronized void addListener(final Listener listener) {
        listeners.add(listener);
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
                for (final Deferred<?> that : chain) {
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
            await();
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
            await(timeout, unit);
        }
        if (cancelled) {
            throw new CancellationException();
        }
        if (!done) {
            throw new TimeoutException();
        }
        return value;
    }

    protected void await() throws InterruptedException {
        if (!done) {
            wait();
        }
    }

    protected void await(final long timeout, final TimeUnit unit) throws InterruptedException {
        if (!done) {
            wait(unit.toMillis(timeout));
        }
    }
}
