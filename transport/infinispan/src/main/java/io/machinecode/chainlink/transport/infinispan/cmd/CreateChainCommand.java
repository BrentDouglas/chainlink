package io.machinecode.chainlink.transport.infinispan.cmd;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.JobRegistry;
import io.machinecode.chainlink.transport.infinispan.ReturnChain;
import org.infinispan.context.InvocationContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CreateChainCommand extends BaseChainlinkCommand {

    public static final byte COMMAND_ID_65 = 65;

    private ChainId chainId;

    public CreateChainCommand(final String cacheName) {
        super(cacheName);
    }

    public CreateChainCommand(final String cacheName, final long jobExecutionId, final ChainId chainId) {
        super(cacheName, jobExecutionId);
        this.chainId = chainId;
    }

    @Override
    public ChainId perform(final InvocationContext context) throws Throwable {
        final JobRegistry jobRegistry = registry.getJobRegistry(jobExecutionId);
        return jobRegistry.registerChain(registry.generateChainId(), new ReturnChain(registry, getOrigin(), jobExecutionId, chainId));
    }

    @Override
    public byte getCommandId() {
        return COMMAND_ID_65;
    }

    @Override
    public boolean isReturnValueExpected() {
        return true;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{ jobExecutionId, chainId };
    }

    @Override
    public void setParameters(final int commandId, final Object[] parameters) {
        super.setParameters(commandId, parameters);
        this.chainId = (ChainId)parameters[1];
    }
}
