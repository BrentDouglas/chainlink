package io.machinecode.chainlink.transport.infinispan;

import io.machinecode.chainlink.core.then.ResolvedChain;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.transport.infinispan.cmd.InvokeChainCommand;
import io.machinecode.chainlink.core.then.ChainImpl;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.PromiseImpl;
import org.infinispan.remoting.transport.Address;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class RemoteChain extends ChainImpl<Void> {

    protected final InfinispanRegistry registry;
    protected final Address address;
    protected final long jobExecutionId;
    protected final ChainId chainId;

    public RemoteChain(final InfinispanRegistry registry, final Address address, final long jobExecutionId, final ChainId chainId) {
        this.registry = registry;
        this.address = address;
        this.jobExecutionId = jobExecutionId;
        this.chainId = chainId;
    }

    protected InvokeChainCommand command(final String name, final boolean willReturn, final Serializable... params) {
        return new InvokeChainCommand(registry.cacheName, jobExecutionId, chainId, name, willReturn, params);
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        try {
            final PromiseImpl<Boolean> promise = new PromiseImpl<Boolean>();
            registry.invoke(address, command("cancel", true, mayInterruptIfRunning), promise);
            return super.cancel(mayInterruptIfRunning) && promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ChainImpl<Void> link(final Chain<?> that) {
        return super.link(new ResolvedChain<Void>(null));
    }

    public void notifyLinkRejected(final Throwable fail) {
        this.promise.reject(fail);
    }

    public void notifyLinkResolved() {
        this.promise.resolve(null);
    }

    @Override
    public Void get() throws InterruptedException, ExecutionException {
        try {
            final PromiseImpl<Void> promise = new PromiseImpl<Void>();
            registry.invoke(address, command("get", true), promise);
            super.get();
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Void get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        try {
            final long end = System.currentTimeMillis() + unit.toMillis(timeout);
            final PromiseImpl<Void> promise = new PromiseImpl<Void>();
            registry.invoke(address, command("get", true, timeout, unit), promise);
            super.get(_tryTimeout(end), MILLISECONDS);
            return promise.get(_tryTimeout(end), MILLISECONDS);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
