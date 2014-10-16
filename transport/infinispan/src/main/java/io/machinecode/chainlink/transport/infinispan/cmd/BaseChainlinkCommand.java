package io.machinecode.chainlink.transport.infinispan.cmd;

import io.machinecode.chainlink.transport.infinispan.InfinispanRegistry;
import io.machinecode.chainlink.transport.infinispan.configuration.ChainlinkCommand;
import org.infinispan.commands.remote.BaseRpcCommand;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class BaseChainlinkCommand extends BaseRpcCommand implements ChainlinkCommand {

    long jobExecutionId;

    transient InfinispanRegistry registry;

    protected BaseChainlinkCommand(final String cacheName) {
        super(cacheName);
    }

    protected BaseChainlinkCommand(final String cacheName, final long jobExecutionId) {
        super(cacheName);
        this.jobExecutionId = jobExecutionId;
    }

    @Override
    public void init(final InfinispanRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{ jobExecutionId };
    }

    @Override
    public void setParameters(final int commandId, final Object[] parameters) {
        if (commandId != getCommandId()) throw new IllegalStateException(); //TODO Message
        this.jobExecutionId = (Long)parameters[0];
    }
}
