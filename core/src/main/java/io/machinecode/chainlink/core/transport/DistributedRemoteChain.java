package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.core.then.ChainImpl;
import io.machinecode.chainlink.core.transport.cmd.Command;
import io.machinecode.chainlink.core.transport.cmd.InvokeChainCommand;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.then.Chain;
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
public class DistributedRemoteChain extends ChainImpl<Void> {

    private static final Logger log = Logger.getLogger(DistributedRemoteChain.class);

    protected final DistributedTransport<?> transport;
    protected final Object address;
    protected final long jobExecutionId;
    protected final ChainId chainId;
    protected final long timeout;
    protected final TimeUnit unit;

    public DistributedRemoteChain(final DistributedTransport<?> transport, final Object address, final long jobExecutionId, final ChainId chainId) {
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
    public void resolve(final Void value) {
        try {
            transport.invokeRemote(address, this.<Void>command("resolve", new Serializable[]{null}), this.timeout, this.unit)
                    .get(timeout, unit);
        } catch (final InterruptedException | ExecutionException | CancellationException | TimeoutException e) {
            log().errorf(e, ""); //TODO Message
        } finally {
            super.resolve(value);
        }
    }

    @Override
    public void reject(final Throwable failure) {
        try {
            transport.invokeRemote(address, this.<Void>command("reject", failure), this.timeout, this.unit)
                    .get(timeout, unit);
        } catch (final InterruptedException | ExecutionException | CancellationException | TimeoutException e) {
            log().errorf(e, ""); //TODO Message
        } finally {
            super.reject(failure);
        }
    }

    @Override
    public void link(final Chain<?> that) {
        try {
            transport.invokeRemote(address, this.<Boolean>command("link", new Serializable[]{null}), this.timeout, this.unit)
                    .get(timeout, unit);
        } catch (final InterruptedException | ExecutionException | CancellationException | TimeoutException e) {
            log().errorf(e, ""); //TODO Message
        } finally {
            super.link(that);
        }
    }

    @Override
    public void linkAndResolve(final Void value, final Chain<?> link) {
        try {
            transport.invokeRemote(address, this.<Void>command("linkAndResolve", null, null), this.timeout, this.unit)
                    .get(timeout, unit);
        } catch (final InterruptedException | ExecutionException | CancellationException | TimeoutException e) {
            log().errorf(e, ""); //TODO Message
        } finally {
            super.link(link);
            super.resolve(value);
        }
    }

    @Override
    public void linkAndReject(final Throwable failure, final Chain<?> link) {
        try {
            transport.invokeRemote(address, this.<Void>command("linkAndReject", failure, null), this.timeout, this.unit)
                    .get(timeout, unit);
        } catch (final InterruptedException | ExecutionException | CancellationException | TimeoutException e) {
            log().errorf(e, ""); //TODO Message
        } finally {
            super.link(link);
            super.reject(failure);
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
