package io.machinecode.nock.core.work;

import io.machinecode.nock.spi.transport.Executable;
import io.machinecode.nock.spi.transport.Result;
import io.machinecode.nock.spi.transport.Synchronization;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.Deferred;
import org.jboss.logging.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public abstract class ExecutableImpl extends SynchronisationImpl implements Executable {

    private static final Logger log = Logger.getLogger(ExecutableImpl.class);

    private final List<Synchronization> synchronizations = new LinkedList<Synchronization>();

    private volatile boolean running = false;
    private volatile boolean cancelled = false;
    private volatile boolean done = false;
    private volatile Deferred<?> chain;

    @Override
    public synchronized ExecutableImpl register(final Synchronization synchronization) {
        if (running) {
            throw new IllegalStateException();
        }
        synchronization.take();
        synchronizations.add(synchronization);
        return this;
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
        try {
            while (!available()) {
                synchronized (this) {
                    wait();
                }
            }
            chain = run(transport);
            chain.resolve(null);
            return ResultImpl.FINISHED;
        } catch (final Exception e) {
            log.debug("", e); //TODO Message
            return new ResultImpl(e);
        } finally {
            resolve(null);
            for (final Synchronization synchronization : synchronizations) {
                synchronization.release();
            }
        }
    }

    @Override
    public synchronized void resolve(final Void _) {
        this.done = true;
        notifyAll();
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
        try {
            if (chain != null) {
                chain.cancel(mayInterruptIfRunning);
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

    public abstract Deferred<?> run(Transport transport) throws Exception;
}
