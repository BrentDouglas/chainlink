package io.machinecode.chainlink.transport.core;

import io.machinecode.chainlink.core.then.ChainImpl;
import io.machinecode.chainlink.core.then.ResolvedChain;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.then.core.DeferredImpl;
import org.jboss.logging.Logger;

import java.io.Serializable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class DistributedLocalChain<A> extends ChainImpl<Void> {

    private static final Logger log = Logger.getLogger(DistributedLocalChain.class);

    protected final Transport<A> transport;
    protected final A address;
    protected final long jobExecutionId;
    protected final ChainId chainId;
    protected final long timeout;
    protected final TimeUnit unit;

    // This is to head off any delayed calls to #get after the job has finished and the registry has been cleaned
    volatile boolean waited = false;

    public DistributedLocalChain(final Transport<A> transport, final A address, final long jobExecutionId, final ChainId chainId) {
        this.transport = transport;
        this.address = address;
        this.jobExecutionId = jobExecutionId;
        this.chainId = chainId;
        this.timeout = transport.getTimeout();
        this.unit = transport.getTimeUnit();
    }

    protected abstract <T> Command<T, A> command(final String name, final Serializable... params);

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        boolean cancelled = false;
        try {
            final DeferredImpl<Boolean, Throwable, Void> promise = new DeferredImpl<>();
            try {
                transport.invokeRemote(address, this.<Boolean>command("cancel", mayInterruptIfRunning), promise);
            } finally {
                cancelled = promise.get(timeout, unit);
            }
        } catch (final InterruptedException | ExecutionException | CancellationException | TimeoutException e) {
            log.warn("", e);
            // Swallow transmission errors
        }
        return super.cancel(mayInterruptIfRunning) && cancelled;
    }

    /*
    @Override
    public void notifyLinked() {
        try {
            final DeferredImpl<Boolean, Throwable, Void> promise = new DeferredImpl<>();
            try {
                transport.invokeRemote(address, this.<Boolean>command("notifyLinked"), promise);
            } finally {
                promise.get(timeout, unit);
            }
        } catch (final InterruptedException | ExecutionException | CancellationException | TimeoutException e) {
            log.warn("", e);
            // Swallow transmission errors
        } finally {
            super.notifyLinked();
        }
    }
    */

    @Override
    public void link(final Chain<?> link) {
        super.link(new ResolvedChain<Void>(null));
    }

    @Override
    public Void get() throws InterruptedException, ExecutionException {
        if (waited) {
            return super.get();
        }
        try {
            final long end = System.currentTimeMillis() + unit.toMillis(timeout);
            final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<>();
            transport.invokeRemote(address, this.<Void>command("get"), promise, _tryTimeout(end), MILLISECONDS);
            try {
                super.get();
            } finally {
                promise.get(_tryTimeout(end), MILLISECONDS);
            }
            return null;
        } catch (final TimeoutException e) {
            throw new ExecutionException(e);
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
            final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<>();
            final long millis = _tryTimeout(end);
            transport.invokeRemote(address, this.<Void>command("get", millis, MILLISECONDS), promise, _tryTimeout(end), MILLISECONDS);
            try {
                super.get(_tryTimeout(end), MILLISECONDS);
            } finally {
                promise.get(_tryTimeout(end), MILLISECONDS);
            }
            return null;
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
