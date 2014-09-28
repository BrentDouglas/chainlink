package io.machinecode.chainlink.transport.jgroups;

import io.machinecode.chainlink.core.then.ChainImpl;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.transport.jgroups.cmd.InvokeChainCommand;
import io.machinecode.then.core.PromiseImpl;
import org.jgroups.Address;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JGroupsRemoteChain extends ChainImpl<Void> {

    protected final JGroupsRegistry registry;
    protected final Address address;
    protected final long jobExecutionId;
    protected final ChainId chainId;

    public JGroupsRemoteChain(final JGroupsRegistry registry, final Address address, final long jobExecutionId, final ChainId chainId) {
        this.registry = registry;
        this.address = address;
        this.jobExecutionId = jobExecutionId;
        this.chainId = chainId;
    }

    protected <T> InvokeChainCommand<T> command(final String name, final Serializable... params) {
        return new InvokeChainCommand<T>(jobExecutionId, chainId, name, params);
    }

    @Override
    public void resolve(final Void value) {
        try {
            final PromiseImpl<Void,Throwable> promise = new PromiseImpl<Void,Throwable>();
            registry.invoke(address, command("resolve", new Serializable[]{ null }), promise);
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
            registry.invoke(address, command("reject", failure), promise);
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
            registry.invoke(address, command("link", new Serializable[]{ null }), promise);
            promise.get();
        } catch (final Exception e) {
            // Swallow transmission errors
        }
        super.link(that);
        return this;
    }

    @Override
    public String toString() {
        return "JGroupsRemoteChain{" +
                "address=" + address +
                ", jobExecutionId=" + jobExecutionId +
                ", chainId=" + chainId +
                '}';
    }
}
