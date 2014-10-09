package io.machinecode.chainlink.transport.core.cmd;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.chainlink.transport.core.DistributedRemoteChain;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
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
    public ChainId perform(final Transport<A> transport, final A origin) throws Throwable {
        final Registry registry = transport.getRegistry();
        final ChainId remoteId = transport.generateChainId();
        registry.registerChain(jobExecutionId, remoteId, createRemoteChain(transport, origin));
        return remoteId;
    }

    protected abstract DistributedRemoteChain<A> createRemoteChain(final Transport<A> transport, final A address);
}
