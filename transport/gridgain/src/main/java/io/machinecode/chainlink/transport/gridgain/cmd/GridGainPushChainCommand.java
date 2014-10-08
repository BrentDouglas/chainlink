package io.machinecode.chainlink.transport.gridgain.cmd;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.transport.core.DistributedRemoteChain;
import io.machinecode.chainlink.transport.core.cmd.DistributedCommand;
import io.machinecode.chainlink.transport.core.cmd.PushChainCommand;
import io.machinecode.chainlink.transport.gridgain.GridGainRegistry;
import io.machinecode.chainlink.transport.gridgain.GridGainRemoteChain;

import java.util.UUID;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class GridGainPushChainCommand extends PushChainCommand<UUID,GridGainRegistry> implements DistributedCommand<ChainId,UUID,GridGainRegistry> {

    public GridGainPushChainCommand(final long jobExecutionId, final ChainId chainId) {
        super(jobExecutionId, chainId);
    }

    @Override
    protected DistributedRemoteChain<UUID,GridGainRegistry> createRemoteChain(final GridGainRegistry registry, final UUID address) {
        return new GridGainRemoteChain(registry, address, jobExecutionId, chainId);
    }
}
