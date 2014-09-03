package io.machinecode.chainlink.transport.infinispan.cmd;

import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.transport.infinispan.RemoteChain;
import org.infinispan.context.InvocationContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PushExecutableCommand extends BaseChainlinkCommand {

    public static final byte COMMAND_ID_66 = 66;

    private Executable executable;

    public PushExecutableCommand(final String cacheName) {
        super(cacheName);
    }

    public PushExecutableCommand(final String cacheName, final long jobExecutionId, final Executable executable) {
        super(cacheName, jobExecutionId);
        this.executable = executable;
    }

    @Override
    public Object perform(final InvocationContext context) throws Throwable {
        registry.registerExecutable(jobExecutionId, executable);
        return null;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_ID_66;
    }

    @Override
    public boolean isReturnValueExpected() {
        return false;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{ jobExecutionId, executable };
    }

    @Override
    public void setParameters(final int commandId, final Object[] parameters) {
        super.setParameters(commandId, parameters);
        this.executable = (Executable)parameters[1];
    }
}
