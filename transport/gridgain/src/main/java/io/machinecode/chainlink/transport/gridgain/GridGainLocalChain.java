package io.machinecode.chainlink.transport.gridgain;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.chainlink.core.transport.DistributedLocalChain;
import io.machinecode.chainlink.core.transport.cmd.InvokeChainCommand;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GridGainLocalChain extends DistributedLocalChain<UUID> {

    public GridGainLocalChain(final Transport<UUID> transport, final UUID address, final long jobExecutionId, final ChainId chainId) {
        super(transport, address, jobExecutionId, chainId);
    }

    @Override
    protected <T> InvokeChainCommand<T,UUID> command(final String name, final Serializable... params) {
        return new InvokeChainCommand<>(jobExecutionId, chainId, name, params);
    }
}
