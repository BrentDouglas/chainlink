package io.machinecode.chainlink.transport.core.cmd;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.transport.core.DistributedRegistry;
import io.machinecode.chainlink.transport.core.DistributedRemoteChain;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public abstract class PushChainCommand<A,R extends DistributedRegistry<A,R>> implements DistributedCommand<ChainId,A,R> {
    private static final long serialVersionUID = 1L;

    protected final long jobExecutionId;
    protected final ChainId chainId;

    public PushChainCommand(final long jobExecutionId, final ChainId chainId) {
        this.jobExecutionId = jobExecutionId;
        this.chainId = chainId;
    }

    @Override
    public ChainId perform(final R registry, final A origin) throws Throwable {
        final ChainId remoteId = registry.generateChainId();
        registry.registerChain(jobExecutionId, remoteId, createRemoteChain(registry, origin));
        return remoteId;
    }

    protected abstract DistributedRemoteChain<A,R> createRemoteChain(final R registry, final A address);
}
