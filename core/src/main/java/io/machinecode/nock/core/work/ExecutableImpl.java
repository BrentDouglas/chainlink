package io.machinecode.nock.core.work;

import io.machinecode.nock.jsl.util.ImmutablePair;
import io.machinecode.nock.spi.transport.Executable;
import io.machinecode.nock.spi.transport.Failure;
import io.machinecode.nock.spi.transport.Synchronization;
import io.machinecode.nock.spi.transport.Transport;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public abstract class ExecutableImpl implements Executable, Synchronization {

    private ImmutablePair<Transport, ExecutableImpl> then = null;
    private ImmutablePair<Transport, ExecutableImpl> always = null;
    private ImmutablePair<Transport, ExecutableImpl> cancel = null;
    private ImmutablePair<Transport, FailureImpl> fail = null;
    private final List<Synchronization> synchronizations = new LinkedList<Synchronization>();
    private final List<Future<Void>> chain = new LinkedList<Future<Void>>();

    private final AtomicInteger sync = new AtomicInteger(0);
    private volatile boolean running = false;
    private volatile boolean cancelled = false;
    private volatile boolean done = false;

    public synchronized ExecutableImpl then(final Transport transport, final ExecutableImpl executable) {
        if (running) {
            throw new IllegalStateException();
        }
        then = ImmutablePair.of(transport, executable);
        return this;
    }

    public synchronized ExecutableImpl always(final Transport transport, final ExecutableImpl executable) {
        if (running) {
            throw new IllegalStateException();
        }
        always = ImmutablePair.of(transport, executable);
        return this;
    }

    public synchronized ExecutableImpl cancel(final Transport transport, final ExecutableImpl executable) {
        if (running) {
            throw new IllegalStateException();
        }
        cancel = ImmutablePair.of(transport, executable);
        return this;
    }

    public synchronized ExecutableImpl fail(final Transport transport, final FailureImpl executable) {
        if (running) {
            throw new IllegalStateException();
        }
        fail = ImmutablePair.of(transport, executable);
        return this;
    }

    @Override
    public synchronized ExecutableImpl register(final Synchronization synchronization) {
        if (running) {
            throw new IllegalStateException();
        }
        synchronization.register();
        synchronizations.add(synchronization);
        return this;
    }

    @Override
    public void execute(final Transport transport) {
        synchronized (this) {
            if (cancelled || running) {
                return;
            }
            running = true;
        }
        try {
            while (sync.get() != 0) {
                synchronized (this) {
                    wait();
                }
            }
            run(transport);
            then();
        } catch (final Exception e) {
            fail(e);
        } finally {
            synchronized (this) {
                done = true;
                notifyAll();
            }
            for (final Synchronization synchronization : synchronizations) {
                synchronization.unRegister();
            }
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
        notifyAll();
        cancel.getKey().executeOnThisThread(cancel.getValue());
        synchronized (chain) {
            for (final Future<Void> future : chain) {
                future.cancel(mayInterruptIfRunning);
            }
        }
        return true;
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
    public void register() {
        sync.incrementAndGet();
    }

    @Override
    public int registered() {
        return sync.get();
    }

    @Override
    public synchronized void unRegister() {
        sync.decrementAndGet();
        notifyAll();
    }

    public abstract void run(Transport transport) throws Exception;

    private void fail(final Exception exception) {
        if (fail == null) {
            return;
        }
        final FailureImpl executable = fail.getValue();
        if (always != null) {
            executable.always(always.getKey(), always.getValue());
        }
        synchronized (chain) {
            chain.add(fail.getKey().fail(executable, exception));
        }
    }

    private void then() {
        if (then == null) {
            return;
        }
        final ExecutableImpl executable = then.getValue();
        if (always != null) {
            executable.always(always.getKey(), always.getValue());
        }
        synchronized (chain) {
            chain.add(then.getKey().executeOnAnyThread(executable));
        }
    }
}
