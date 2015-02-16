package io.machinecode.chainlink.core.transport.cmd;

import io.machinecode.chainlink.core.registry.UUIDId;
import io.machinecode.chainlink.core.transport.DistributedRemoteChain;
import io.machinecode.chainlink.core.transport.DistributedTransport;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.transport.Transport;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class PushChainCommand implements Command<ChainId> {
    private static final long serialVersionUID = 1L;

    protected final long jobExecutionId;
    protected final ChainId chainId;

    public PushChainCommand(final long jobExecutionId, final ChainId chainId) {
        this.jobExecutionId = jobExecutionId;
        this.chainId = chainId;
    }

    @Override
    public ChainId perform(final Configuration configuration, final Object origin) throws Throwable {
        final Transport transport = configuration.getTransport();
        //TODO Fix this
        final Chain<?> chain = new DistributedRemoteChain((DistributedTransport<?>)transport, origin, jobExecutionId, chainId);
        final ChainId remoteId = new UUIDId(transport);
        configuration.getRegistry().registerChain(jobExecutionId, remoteId, chain);
        return remoteId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append("{");
        sb.append("jobExecutionId=").append(jobExecutionId);
        sb.append(", chainId=").append(chainId);
        sb.append('}');
        return sb.toString();
    }
}
