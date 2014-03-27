package io.machinecode.chainlink.transport.infinispan.cmd;

import io.machinecode.chainlink.transport.infinispan.InfinispanTransport;
import io.machinecode.chainlink.transport.infinispan.configuration.ChainlinkCommand;
import io.machinecode.chainlink.spi.transport.DeferredId;
import org.infinispan.commands.remote.BaseRpcCommand;
import org.infinispan.remoting.transport.Address;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class DeferredCommand extends BaseRpcCommand implements ChainlinkCommand {

    Address remote;
    long jobExecutionId;
    DeferredId deferredId;

    transient InfinispanTransport transport;

    protected DeferredCommand(final String cacheName) {
        super(cacheName);
    }

    protected DeferredCommand(final String cacheName, final Address remote, final long jobExecutionId, final DeferredId deferredId) {
        super(cacheName);
        this.remote = remote;
        this.jobExecutionId = jobExecutionId;
        this.deferredId = deferredId;
    }

    @Override
    public void setTransport(final InfinispanTransport transport) {
        this.transport = transport;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{ remote, jobExecutionId, deferredId};
    }

    @Override
    public void setParameters(final int commandId, final Object[] parameters) {
        if (commandId != getCommandId()) throw new IllegalStateException(); //TODO Message
        this.remote = (Address)parameters[0];
        this.jobExecutionId = (Long)parameters[1];
        this.deferredId = (DeferredId)parameters[2];
    }

    @Override
    public boolean isReturnValueExpected() {
        return false;
    }
}
