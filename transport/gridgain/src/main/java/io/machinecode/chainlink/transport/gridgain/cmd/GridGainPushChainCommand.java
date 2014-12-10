package io.machinecode.chainlink.transport.gridgain.cmd;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.chainlink.transport.core.DistributedRemoteChain;
import io.machinecode.chainlink.transport.core.cmd.PushChainCommand;
import io.machinecode.chainlink.transport.gridgain.GridGainRemoteChain;

import java.util.UUID;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GridGainPushChainCommand extends PushChainCommand<UUID> {
    private static final long serialVersionUID = 1L;

    public GridGainPushChainCommand(final long jobExecutionId, final ChainId chainId) {
        super(jobExecutionId, chainId);
    }

    @Override
    protected DistributedRemoteChain<UUID> createRemoteChain(final Transport<UUID> transport, final UUID address) {
        return new GridGainRemoteChain(transport, address, jobExecutionId, chainId);
    }
}
