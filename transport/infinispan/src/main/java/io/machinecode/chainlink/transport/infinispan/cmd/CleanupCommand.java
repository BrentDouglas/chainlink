package io.machinecode.chainlink.transport.infinispan.cmd;

import io.machinecode.chainlink.transport.infinispan.InfinispanTransport;
import io.machinecode.chainlink.transport.infinispan.configuration.ChainlinkCommand;
import org.infinispan.commands.remote.BaseRpcCommand;
import org.infinispan.context.InvocationContext;
import org.infinispan.remoting.transport.Address;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CleanupCommand extends BaseRpcCommand implements ChainlinkCommand {

    public static final byte COMMAND_ID = 62;

    Address remote;
    long jobExecutionId;

    transient InfinispanTransport transport;

    public CleanupCommand(final String cacheName) {
        super(cacheName);
    }

    public CleanupCommand(final String cacheName, final Address remote, long jobExecutionId) {
        super(cacheName);
        this.remote = remote;
        this.jobExecutionId = jobExecutionId;
    }

    @Override
    public void setTransport(final InfinispanTransport transport) {
        this.transport = transport;
    }

    @Override
    public Object perform(final InvocationContext invocationContext) throws Throwable {
        transport.unregisterJob(jobExecutionId);
        return true;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_ID;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{ remote, jobExecutionId };
    }

    @Override
    public void setParameters(final int commandId, final Object[] parameters) {
        if (commandId != getCommandId()) throw new IllegalStateException(); //TODO Message
        this.remote = (Address)parameters[0];
        this.jobExecutionId = (Long)parameters[1];
    }

    @Override
    public boolean isReturnValueExpected() {
        return false;
    }
}
