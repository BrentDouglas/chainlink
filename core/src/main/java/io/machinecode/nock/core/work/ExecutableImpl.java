package io.machinecode.nock.core.work;

import gnu.trove.set.hash.THashSet;
import io.machinecode.nock.spi.transport.Executable;
import io.machinecode.nock.spi.transport.Result;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.util.Message;
import io.machinecode.nock.spi.work.Deferred;
import io.machinecode.nock.spi.work.Listener;
import io.machinecode.nock.spi.work.Work;
import org.jboss.logging.Logger;

import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public abstract class ExecutableImpl<T extends Work> implements Executable {

    private static final Logger log = Logger.getLogger(ExecutableImpl.class);

    private volatile boolean running = false;
    private volatile boolean cancelled = false;
    private volatile boolean done = false;
    private volatile Deferred<?> chain;

    private volatile int sync = 0;

    private final Set<Listener> then = new THashSet<Listener>(0);
    private final Set<Listener> cancel = new THashSet<Listener>(0);
    private final Set<Listener> always = new THashSet<Listener>(0);

    protected final T work;
    protected final long jobExecutionId;

    protected ExecutableImpl(final long jobExecutionId, final T work) {
        this.jobExecutionId = jobExecutionId;
        this.work = work;
    }

    @Override
    public long getJobExecutionId() {
        return jobExecutionId;
    }

    @Override
    public Result execute(final Transport transport) {
        synchronized (this) {
            if (cancelled) {
                return ResultImpl.CANCELLED;
            }
            if (running) {
                return ResultImpl.RUNNING;
            }
            running = true;
        }
        log.tracef(Message.format("executable.execute", jobExecutionId));
        try {
            chain = run(transport);
            chain.resolve(null);
            return ResultImpl.FINISHED;
        } catch (final Throwable e) {
            log.infof(e, Message.format("executable.execute.exception", jobExecutionId));
            return new ResultImpl(e);
        } finally {
            resolve(null);
        }
    }

    @Override
    public synchronized void resolve(final Void _) {
        log.tracef(Message.format("executable.resolve", jobExecutionId));
        this.done = true;
        try {
            RuntimeException exception = null;
            synchronized (then) {
                for (final Listener listener : then) {
                    try {
                        log.tracef(Message.format("executable.then.listener.run", jobExecutionId));
                        listener.run(this);
                    } catch (final RuntimeException e) {
                        if (exception == null) {
                            exception = e;
                        } else {
                            exception.addSuppressed(e);
                        }
                    }
                }
            }
            synchronized (always) {
                for (final Listener listener : always) {
                    try {
                        log.tracef(Message.format("executable.always.listener.run", jobExecutionId));
                        listener.run(this);
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
        } finally {
            notifyAll();
        }
    }

    @Override
    public void onResolve(final Listener listener) {
        log.tracef(Message.format("executable.then.listener.add", jobExecutionId));
        synchronized (then) {
            then.add(listener);
        }
    }

    @Override
    public void onCancel(final Listener listener) {
        log.tracef(Message.format("executable.cancel.listener.add", jobExecutionId));
        synchronized (cancel) {
            cancel.add(listener);
        }
    }

    @Override
    public void always(final Listener listener) {
        log.tracef(Message.format("executable.always.listener.add", jobExecutionId));
        synchronized (always) {
            always.add(listener);
        }
    }

    @Override
    public synchronized boolean cancel(final boolean mayInterruptIfRunning) {
        if (!mayInterruptIfRunning && running) {
            return false;
        }
        if (cancelled) {
            return true;
        }
        cancelled = true;
        log.tracef(Message.format("executable.cancel", jobExecutionId));
        try {
            RuntimeException exception = null;
            synchronized (cancel) {
                for (final Listener listener : cancel) {
                    try {
                        log.tracef(Message.format("executable.cancel.listener.run", jobExecutionId));
                        listener.run(this);
                    } catch (final RuntimeException e) {
                        if (exception == null) {
                            exception = e;
                        } else {
                            exception.addSuppressed(e);
                        }
                    }
                }
            }
            synchronized (always) {
                for (final Listener listener : always) {
                    try {
                        log.tracef(Message.format("executable.always.listener.run", jobExecutionId));
                        listener.run(this);
                    } catch (final RuntimeException e) {
                        if (exception == null) {
                            exception = e;
                        } else {
                            exception.addSuppressed(e);
                        }
                    }
                }
            }
            if (chain != null) {
                chain.cancel(mayInterruptIfRunning);
            }
            if (exception != null) {
                throw exception;
            }
            return true;
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
    public Void get() throws InterruptedException, ExecutionException, CancellationException {
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
        return null;
    }

    @Override
    public Void get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException, CancellationException {
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
        return null;
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

    public abstract Deferred<?> run(Transport transport) throws Exception;
}
