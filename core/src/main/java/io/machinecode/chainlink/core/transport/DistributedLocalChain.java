package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.core.then.ChainImpl;
import io.machinecode.chainlink.core.then.ResolvedChain;
import io.machinecode.chainlink.core.transport.cmd.Command;
import io.machinecode.chainlink.core.transport.cmd.InvokeChainCommand;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.then.api.Promise;
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
public class DistributedLocalChain extends ChainImpl<Void> {

    private static final Logger log = Logger.getLogger(DistributedLocalChain.class);

    protected final BaseTransport<?> transport;
    protected final Object address;
    protected final long jobExecutionId;
    protected final ChainId chainId;
    protected final long timeout;
    protected final TimeUnit unit;

    // This is to head off any delayed calls to #get after the job has finished and the registry has been cleaned
    volatile boolean waited = false;

    public DistributedLocalChain(final BaseTransport<?> transport, final Object address, final long jobExecutionId, final ChainId chainId) {
        this.transport = transport;
        this.address = address;
        this.jobExecutionId = jobExecutionId;
        this.chainId = chainId;
        this.timeout = transport.getTimeout();
        this.unit = transport.getTimeUnit();
    }

    protected <T> Command<T> command(final String name, final Serializable... params) {
        return new InvokeChainCommand<>(jobExecutionId, chainId, name, params);
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        boolean cancelled = false;
        try {
            cancelled = transport.invokeRemote(address, this.<Boolean>command("cancel", mayInterruptIfRunning), this.timeout, this.unit)
                    .get(timeout, unit);
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
            final Promise<Void,Throwable,Object> promise =  transport.invokeRemote(address, this.<Void>command("get"), _tryTimeout(end), MILLISECONDS);
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
            final long millis = _tryTimeout(end);
            final Promise<Void, Throwable, Object> promise = transport.invokeRemote(address, this.<Void>command("get", millis, MILLISECONDS), _tryTimeout(end), MILLISECONDS);
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
