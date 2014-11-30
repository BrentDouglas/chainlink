package io.machinecode.chainlink.transport.infinispan.cmd;

import org.infinispan.context.InvocationContext;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class CleanupCommand extends BaseChainlinkCommand {

    public static final byte COMMAND_ID_62 = 62;

    public CleanupCommand(final String cacheName) {
        super(cacheName);
    }

    public CleanupCommand(final String cacheName, long jobExecutionId) {
        super(cacheName, jobExecutionId);
    }

    @Override
    public Object perform(final InvocationContext invocationContext) throws Throwable {
        registry.unregisterJob(jobExecutionId).get();
        return true;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_ID_62;
    }

    @Override
    public boolean isReturnValueExpected() {
        return false;
    }
}
