package io.machinecode.chainlink.transport.gridgain;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.transport.core.DistributedRemoteChain;
import io.machinecode.chainlink.transport.core.cmd.InvokeChainCommand;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class GridGainRemoteChain extends DistributedRemoteChain<UUID,GridGainRegistry> {

    public GridGainRemoteChain(final GridGainRegistry registry, final UUID address, final long jobExecutionId, final ChainId chainId) {
        super(registry, address, jobExecutionId, chainId);
    }

    @Override
    protected <T> InvokeChainCommand<T,UUID,GridGainRegistry> command(final String name, final Serializable... params) {
        return new InvokeChainCommand<T,UUID,GridGainRegistry>(jobExecutionId, chainId, name, params);
    }
}
