package io.machinecode.chainlink.transport.infinispan;

import io.machinecode.chainlink.core.then.ChainImpl;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.transport.infinispan.cmd.InvokeChainCommand;
import io.machinecode.then.core.DeferredImpl;
import org.infinispan.remoting.transport.Address;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InfinispanRemoteChain extends ChainImpl<Void> {

    protected final InfinispanRegistry registry;
    protected final Address address;
    protected final long jobExecutionId;
    protected final ChainId chainId;

    public InfinispanRemoteChain(final InfinispanRegistry registry, final Address address, final long jobExecutionId, final ChainId chainId) {
        this.registry = registry;
        this.address = address;
        this.jobExecutionId = jobExecutionId;
        this.chainId = chainId;
    }

    protected InvokeChainCommand command(final String name, final boolean willReturn, final Serializable... params) {
        return new InvokeChainCommand(registry.cacheName, jobExecutionId, chainId, name, willReturn, params);
    }

    @Override
    public void resolve(final Void value) {
        try {
            final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<Void,Throwable,Void>();
            registry.invoke(address, command("resolve", false, new Serializable[]{ null }), promise);
            promise.get();
        } catch (final Exception e) {
            // Swallow transmission errors
        }
        super.resolve(value);
    }

    @Override
    public void reject(final Throwable failure) {
        try {
            final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<Void,Throwable,Void>();
            registry.invoke(address, command("reject", false, failure), promise);
            promise.get();
        } catch (final Exception e) {
            // Swallow transmission errors
        }
        super.reject(failure);
    }

    @Override
    public ChainImpl<Void> link(final Chain<?> that) {
        try {
            final DeferredImpl<Boolean,Throwable,Void> promise = new DeferredImpl<Boolean,Throwable,Void>();
            registry.invoke(address, command("link", false, new Serializable[]{ null }), promise);
            promise.get();
        } catch (final Exception e) {
            // Swallow transmission errors
        }
        super.link(that);
        return this;
    }

    @Override
    public String toString() {
        return "InfinispanRemoteChain{" +
                "address=" + address +
                ", jobExecutionId=" + jobExecutionId +
                ", chainId=" + chainId +
                '}';
    }
}
