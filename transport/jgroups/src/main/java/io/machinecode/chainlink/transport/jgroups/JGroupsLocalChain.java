package io.machinecode.chainlink.transport.jgroups;

import io.machinecode.chainlink.core.then.ChainImpl;
import io.machinecode.chainlink.core.then.ResolvedChain;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.transport.jgroups.cmd.InvokeChainCommand;
import io.machinecode.then.core.PromiseImpl;
import org.jgroups.Address;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JGroupsLocalChain extends ChainImpl<Void> {
    protected final JGroupsRegistry registry;
    protected final Address address;
    protected final long jobExecutionId;
    protected final ChainId chainId;

    // This is to head off any delayed calls to #get after the job has finished and the registry has been cleaned
    volatile boolean waited = false;

    public JGroupsLocalChain(final JGroupsRegistry registry, final Address address, final long jobExecutionId, final ChainId chainId) {
        this.registry = registry;
        this.address = address;
        this.jobExecutionId = jobExecutionId;
        this.chainId = chainId;
    }

    protected <T> InvokeChainCommand<T> command(final String name, final Serializable... params) {
        return new InvokeChainCommand<T>(jobExecutionId, chainId, name, params);
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        boolean cancelled = false;
        try {
            final PromiseImpl<Boolean, Throwable> promise = new PromiseImpl<Boolean, Throwable>();
            registry.invoke(address, command("cancel", mayInterruptIfRunning), promise);
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
        return "JGroupsLocalChain{" +
                "address=" + address +
                ", jobExecutionId=" + jobExecutionId +
                ", chainId=" + chainId +
                ", waited=" + waited +
                '}';
    }
}
