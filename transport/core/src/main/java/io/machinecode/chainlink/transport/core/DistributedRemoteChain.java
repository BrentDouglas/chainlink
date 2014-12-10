package io.machinecode.chainlink.transport.core;

import io.machinecode.chainlink.core.then.ChainImpl;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.then.api.Deferred;
import io.machinecode.then.core.DeferredImpl;
import org.jboss.logging.Logger;

import java.io.Serializable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class DistributedRemoteChain<A> extends ChainImpl<Void> {

    private static final Logger log = Logger.getLogger(DistributedRemoteChain.class);

    protected final Transport<A> transport;
    protected final A address;
    protected final long jobExecutionId;
    protected final ChainId chainId;
    protected final long timeout;
    protected final TimeUnit unit;

    public DistributedRemoteChain(final Transport<A> transport, final A address, final long jobExecutionId, final ChainId chainId) {
        this.transport = transport;
        this.address = address;
        this.jobExecutionId = jobExecutionId;
        this.chainId = chainId;
        this.timeout = transport.getTimeout();
        this.unit = transport.getTimeUnit();
    }

    protected abstract <T> Command<T, A> command(final String name, final Serializable... params);

    @Override
    public void resolve(final Void value) {
        try {
            final Deferred<Void,Throwable,Void> promise = new DeferredImpl<>();
            try {
                transport.invokeRemote(address, this.<Void>command("resolve", new Serializable[]{null}), promise);
            } finally {
                promise.get(timeout, unit);
            }
        } catch (final InterruptedException | ExecutionException | CancellationException | TimeoutException e) {
            log().errorf(e, ""); //TODO Message
        } finally {
            super.resolve(value);
        }
    }

    @Override
    public void reject(final Throwable failure) {
        try {
            final Deferred<Void,Throwable,Void> promise = new DeferredImpl<>();
            try {
                transport.invokeRemote(address, this.<Void>command("reject", failure), promise);
            } finally {
                promise.get(timeout, unit);
            }
        } catch (final InterruptedException | ExecutionException | CancellationException | TimeoutException e) {
            log().errorf(e, ""); //TODO Message
        } finally {
            super.reject(failure);
        }
    }

    @Override
    public void link(final Chain<?> that) {
        try {
            final Deferred<Boolean,Throwable,Void> promise = new DeferredImpl<>();
            try {
                transport.invokeRemote(address, this.<Boolean>command("link", new Serializable[]{null}), promise);
            } finally {
                promise.get(timeout, unit);
            }
        } catch (final InterruptedException | ExecutionException | CancellationException | TimeoutException e) {
            log().errorf(e, ""); //TODO Message
        } finally {
            super.link(that);
        }
    }

    @Override
    public void linkAndResolve(final Void value, final Chain<?> link) {
        try {
            final Deferred<Void,Throwable,Void> promise = new DeferredImpl<>();
            try {
                transport.invokeRemote(address, this.<Void>command("linkAndResolve", null, null), promise);
            } finally {
                promise.get(timeout, unit);
            }
        } catch (final InterruptedException | ExecutionException | CancellationException | TimeoutException e) {
            log().errorf(e, ""); //TODO Message
        } finally {
            super.linkAndResolve(value, link);
        }
    }

    @Override
    public void linkAndReject(final Throwable failure, final Chain<?> link) {
        try {
            final Deferred<Void,Throwable,Void> promise = new DeferredImpl<>();
            try {
                transport.invokeRemote(address, this.<Void>command("linkAndReject", failure, null), promise);
            } finally {
                promise.get(timeout, unit);
            }
        } catch (final InterruptedException | ExecutionException | CancellationException | TimeoutException e) {
            log().errorf(e, ""); //TODO Message
        } finally {
            super.linkAndReject(failure, link);
        }
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "address=" + address +
                ", jobExecutionId=" + jobExecutionId +
                ", chainId=" + chainId +
                '}';
    }
}
