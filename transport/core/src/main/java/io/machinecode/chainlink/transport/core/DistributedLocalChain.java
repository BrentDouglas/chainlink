package io.machinecode.chainlink.transport.core;

import io.machinecode.chainlink.core.then.ChainImpl;
import io.machinecode.chainlink.core.then.ResolvedChain;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.transport.core.cmd.DistributedCommand;
import io.machinecode.then.core.PromiseImpl;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class DistributedLocalChain<A, R extends DistributedRegistry<A, R>> extends ChainImpl<Void> {
    protected final R registry;
    protected final A address;
    protected final long jobExecutionId;
    protected final ChainId chainId;

    // This is to head off any delayed calls to #get after the job has finished and the registry has been cleaned
    volatile boolean waited = false;

    public DistributedLocalChain(final R registry, final A address, final long jobExecutionId, final ChainId chainId) {
        this.registry = registry;
        this.address = address;
        this.jobExecutionId = jobExecutionId;
        this.chainId = chainId;
    }

    protected abstract <T> DistributedCommand<T, A, R> command(final String name, final Serializable... params);

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        boolean cancelled = false;
        try {
            final PromiseImpl<Boolean, Throwable> promise = new PromiseImpl<Boolean, Throwable>();
            registry.invoke(address, this.<Boolean>command("cancel", mayInterruptIfRunning), promise);
            cancelled = promise.get();
        } catch (final Exception e) {
            // Swallow transmission errors
        }
        return super.cancel(mayInterruptIfRunning) && cancelled;
    }

    @Override
    public ChainImpl<Void> link(final Chain<?> that) {
        return super.link(new ResolvedChain<Void>(null));
    }

    @Override
    public Void get() throws InterruptedException, ExecutionException {
        if (waited) {
            return super.get();
        }
        try {
            final PromiseImpl<Void,Throwable> promise = new PromiseImpl<Void,Throwable>();
            registry.invoke(address, this.<Void>command("get"), promise, 0, MILLISECONDS);
            try {
                super.get();
            } finally {
                return promise.get();
            }
        } finally {
            waited = true;
        }
    }

    @Override
    public Void get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (waited) {
            return super.get(timeout, unit);
        }
        try {
            final long end = System.currentTimeMillis() + unit.toMillis(timeout);
            final PromiseImpl<Void,Throwable> promise = new PromiseImpl<Void,Throwable>();
            final long millis = _tryTimeout(end);
            registry.invoke(address, this.<Void>command("get", millis, MILLISECONDS), promise, _tryTimeout(end), MILLISECONDS);
            try {
                super.get(_tryTimeout(end), MILLISECONDS);
            } finally {
                return promise.get(_tryTimeout(end), MILLISECONDS);
            }
        } finally {
            waited = true;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "address=" + address +
                ", jobExecutionId=" + jobExecutionId +
                ", chainId=" + chainId +
                ", waited=" + waited +
                '}';
    }
}
