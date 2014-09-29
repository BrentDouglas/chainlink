package io.machinecode.chainlink.transport.core;

import io.machinecode.chainlink.core.then.ChainImpl;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.transport.core.cmd.DistributedCommand;
import io.machinecode.then.core.PromiseImpl;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class DistributedRemoteChain<A, R extends DistributedInvoker<A, R> & Registry> extends ChainImpl<Void> {

    protected final R registry;
    protected final A address;
    protected final long jobExecutionId;
    protected final ChainId chainId;

    public DistributedRemoteChain(final R registry, final A address, final long jobExecutionId, final ChainId chainId) {
        this.registry = registry;
        this.address = address;
        this.jobExecutionId = jobExecutionId;
        this.chainId = chainId;
    }

    protected abstract <T> DistributedCommand<T, A, R> command(final String name, final Serializable... params);

    @Override
    public void resolve(final Void value) {
        try {
            final PromiseImpl<Void,Throwable> promise = new PromiseImpl<Void,Throwable>();
            registry.invoke(address, this.<Void>command("resolve", new Serializable[]{null}), promise);
            promise.get();
        } catch (final Exception e) {
            // Swallow transmission errors
        }
        super.resolve(value);
    }

    @Override
    public void reject(final Throwable failure) {
        try {
            final PromiseImpl<Void,Throwable> promise = new PromiseImpl<Void,Throwable>();
            registry.invoke(address, this.<Void>command("reject", failure), promise);
            promise.get();
        } catch (final Exception e) {
            // Swallow transmission errors
        }
        super.reject(failure);
    }

    @Override
    public ChainImpl<Void> link(final Chain<?> that) {
        try {
            final PromiseImpl<Boolean,Throwable> promise = new PromiseImpl<Boolean,Throwable>();
            registry.invoke(address, this.<Boolean>command("link", new Serializable[]{null}), promise);
            promise.get();
        } catch (final Exception e) {
            // Swallow transmission errors
        }
        super.link(that);
        return this;
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
