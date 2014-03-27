package io.machinecode.chainlink.transport.infinispan.cmd;

import io.machinecode.chainlink.core.deferred.LinkedDeferred;
import io.machinecode.chainlink.core.execution.UUIDDeferredId;
import io.machinecode.chainlink.jsl.core.util.ImmutablePair;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.transport.DeferredId;
import io.machinecode.chainlink.spi.util.Pair;
import io.machinecode.chainlink.transport.infinispan.InfinispanTransport;
import io.machinecode.chainlink.transport.infinispan.RemoteDeferred;
import io.machinecode.chainlink.transport.infinispan.configuration.ChainlinkCommand;
import org.infinispan.commands.remote.BaseRpcCommand;
import org.infinispan.context.InvocationContext;
import org.infinispan.remoting.transport.Address;

import java.util.UUID;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CreateDeferredCommand extends BaseRpcCommand implements ChainlinkCommand {

    public static final byte COMMAND_ID = 65;

    Executable executable;

    transient InfinispanTransport transport;

    public CreateDeferredCommand(final String cacheName) {
        super(cacheName);
    }

    public CreateDeferredCommand(final String cacheName, final Executable executable) {
        super(cacheName);
        this.executable = executable;
    }

    @Override
    public DeferredId perform(final InvocationContext context) throws Throwable {
        final long jobExecutionId = executable.getContext().getJobExecutionId();
        final DeferredId deferredId = new UUIDDeferredId(UUID.randomUUID());
        final Deferred<?> deferred = new LinkedDeferred<Void>();
        transport.registerDeferred(jobExecutionId, deferredId, deferred);
        return deferredId;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_ID;
    }

    @Override
    public void setTransport(final InfinispanTransport transport) {
        this.transport = transport;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{ executable };
    }

    @Override
    public void setParameters(final int commandId, final Object[] parameters) {
        if (commandId != getCommandId()) throw new IllegalStateException(); //TODO Message
        this.executable = (Executable)parameters[0];
    }

    @Override
    public boolean isReturnValueExpected() {
        return true;
    }
}
