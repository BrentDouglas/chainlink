package io.machinecode.chainlink.core.transport.cmd;

import io.machinecode.chainlink.core.transport.DistributedRemoteChain;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.spi.transport.Transport;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class PushChainCommand<A> implements Command<ChainId,A> {
    private static final long serialVersionUID = 1L;

    protected final long jobExecutionId;
    protected final ChainId chainId;

    public PushChainCommand(final long jobExecutionId, final ChainId chainId) {
        this.jobExecutionId = jobExecutionId;
        this.chainId = chainId;
    }

    @Override
    public ChainId perform(final Transport<A> transport, final Registry registry, final A origin) throws Throwable {
        final Chain<?> chain = createRemoteChain(transport, origin);
        final ChainId remoteId = transport.generateChainId();
        registry.registerChain(jobExecutionId, remoteId, chain);
        return remoteId;
    }

    protected abstract DistributedRemoteChain<A> createRemoteChain(final Transport<A> transport, final A address);

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append("{");
        sb.append("jobExecutionId=").append(jobExecutionId);
        sb.append(", chainId=").append(chainId);
        sb.append('}');
        return sb.toString();
    }
}
