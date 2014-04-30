package io.machinecode.chainlink.transport.infinispan.cmd;

import io.machinecode.chainlink.spi.registry.ChainId;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class DeferredCommand extends BaseChainlinkCommand {

    ChainId chainId;

    protected DeferredCommand(final String cacheName) {
        super(cacheName);
    }

    protected DeferredCommand(final String cacheName, final long jobExecutionId, final ChainId chainId) {
        super(cacheName, jobExecutionId);
        this.chainId = chainId;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{ jobExecutionId, chainId};
    }

    @Override
    public void setParameters(final int commandId, final Object[] parameters) {
        super.setParameters(commandId, parameters);
        this.chainId = (ChainId)parameters[1];
    }

    @Override
    public boolean isReturnValueExpected() {
        return false;
    }
}
