package io.machinecode.nock.core.work;

import gnu.trove.set.hash.THashSet;
import io.machinecode.nock.spi.util.Message;
import io.machinecode.nock.spi.work.Deferred;
import io.machinecode.nock.spi.work.Listener;
import org.jboss.logging.Logger;

import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class DeferredImpl<T> implements Deferred<T> {

    private static final Logger log = Logger.getLogger(DeferredImpl.class);

    protected final Deferred<?>[] chain;

    protected volatile boolean cancelled = false;
    protected volatile boolean done = false;

    private volatile int sync = 0;

    private final Set<Listener> then = new THashSet<Listener>(0);
    private final Set<Listener> cancel = new THashSet<Listener>(0);
    private final Set<Listener> always = new THashSet<Listener>(0);

    protected volatile T value;

    public DeferredImpl(final Deferred<?>... chain) {
        this.chain = chain;
    }

    @Override
    public synchronized void resolve(final T that) {
        log.tracef(Message.format("deferred.resolve"));
        this.done = true;
        this.value = that;
        try {
            RuntimeException exception = null;
            for (final Listener listener : then) {
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
            for (final Listener listener : always) {
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
        } finally {
            notifyAll();
        }
    }

    @Override
    public synchronized void onResolve(final Listener listener) {
        then.add(listener);
    }

    @Override
    public synchronized void onCancel(final Listener listener) {
        cancel.add(listener);
    }

    @Override
    public synchronized void always(final Listener listener) {
        always.add(listener);
    }

    @Override
    public synchronized boolean cancel(final boolean mayInterruptIfRunning) {
        if (this.cancelled) {
            return true;
        }
        this.cancelled = true;
        log.tracef(Message.format("deferred.cancel"));
        try {
            boolean cancelled = true;
            RuntimeException exception = null;
            for (final Listener listener : cancel) {
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
            for (final Listener listener : always) {
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

    @Override
    public void enlist() {
        ++sync;
    }

    @Override
    public void delist() {
        --sync;
    }

    @Override
    public boolean available() {
        return sync == 0;
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
