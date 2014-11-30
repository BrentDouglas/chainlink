package io.machinecode.chainlink.transport.infinispan.cmd;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.transport.infinispan.InfinispanRemoteChain;
import org.infinispan.context.InvocationContext;
import org.jboss.logging.Logger;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class PushChainCommand extends BaseChainlinkCommand {

    private static final Logger log = Logger.getLogger(PushChainCommand.class);

    public static final byte COMMAND_ID_65 = 65;

    private ChainId chainId;

    public PushChainCommand(final String cacheName) {
        super(cacheName);
    }

    public PushChainCommand(final String cacheName, final long jobExecutionId, final ChainId chainId) {
        super(cacheName, jobExecutionId);
        this.chainId = chainId;
    }

    @Override
    public ChainId perform(final InvocationContext context) throws Throwable {
        final ChainId remoteId = registry.generateChainId();
        registry.registerChain(jobExecutionId, remoteId, new InfinispanRemoteChain(registry, getOrigin(), jobExecutionId, chainId));
        return remoteId;
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
