package io.machinecode.chainlink.transport.gridgain;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.transport.core.DistributedLocalChain;
import io.machinecode.chainlink.transport.core.cmd.InvokeChainCommand;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class GridGainLocalChain extends DistributedLocalChain<UUID,GridGainRegistry> {

    public GridGainLocalChain(final GridGainRegistry registry, final UUID address, final long jobExecutionId, final ChainId chainId) {
        super(registry, address, jobExecutionId, chainId);
    }

    @Override
    protected <T> InvokeChainCommand<T,UUID,GridGainRegistry> command(final String name, final Serializable... params) {
        return new InvokeChainCommand<T,UUID,GridGainRegistry>(jobExecutionId, chainId, name, params);
    }
}
