package io.machinecode.chainlink.transport.infinispan;

import io.machinecode.chainlink.core.then.ChainImpl;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.then.OnLink;
import io.machinecode.chainlink.transport.infinispan.cmd.InvokeChainCommand;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.PromiseImpl;
import org.infinispan.remoting.transport.Address;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ReturnChain extends ChainImpl<Void> {

    protected final InfinispanRegistry registry;
    protected final Address address;
    protected final long jobExecutionId;
    protected final ChainId chainId;
    protected final Notifier promise;

    public ReturnChain(final InfinispanRegistry registry, final Address address, final long jobExecutionId, final ChainId chainId) {
        this.registry = registry;
        this.address = address;
        this.jobExecutionId = jobExecutionId;
        this.chainId = chainId;
        this.promise = new Notifier();
        this.onLink(this.promise);
    }

    protected InvokeChainCommand command(final String name, final boolean willReturn, final Serializable... params) {
        return new InvokeChainCommand(registry.cacheName, jobExecutionId, chainId, name, willReturn, params);
    }

    @Override
    public void resolve(final Void value) {
        try {
            final PromiseImpl<Boolean> promise = new PromiseImpl<Boolean>();
            registry.invoke(address, command("resolve", false, new Serializable[]{ null }), promise);
            super.resolve(value);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reject(final Throwable failure) {
        try {
            final PromiseImpl<Boolean> promise = new PromiseImpl<Boolean>();
            registry.invoke(address, command("reject", false, failure), promise);
            super.reject(failure);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ChainImpl<Void> link(final Chain<?> that) {
        try {
            final PromiseImpl<Boolean> promise = new PromiseImpl<Boolean>();
            registry.invoke(address, command("link", false, new Serializable[]{ null }), promise);
            super.link(that);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public Promise<Void> await() {
        return this.promise;
    }

    public class Notifier extends PromiseImpl<Void> implements OnLink {

        @Override
        public void reject(final Throwable fail) {
            try {
                final PromiseImpl<Void> promise = new PromiseImpl<Void>();
                registry.invoke(address, command("notifyLinkRejected", false, fail), promise);
                super.reject(fail);
                promise.get();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void resolve(final Void that) {
            try {
                final PromiseImpl<Void> promise = new PromiseImpl<Void>();
                registry.invoke(address, command("notifyLinkResolved", false), promise);
                super.resolve(that);
                promise.get();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void link(final Chain<?> chain) {
            chain.await()
                    .onResolve(this)
                    .onReject(this);
        }
    }
}
