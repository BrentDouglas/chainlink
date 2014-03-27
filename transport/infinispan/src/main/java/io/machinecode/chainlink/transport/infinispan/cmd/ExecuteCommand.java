package io.machinecode.chainlink.transport.infinispan.cmd;

import io.machinecode.chainlink.core.deferred.LinkedDeferred;
import io.machinecode.chainlink.core.execution.UUIDDeferredId;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.transport.infinispan.CommandListener;
import io.machinecode.chainlink.transport.infinispan.InfinispanTransport;
import io.machinecode.chainlink.transport.infinispan.configuration.ChainlinkCommand;
import io.machinecode.chainlink.spi.transport.DeferredId;
import io.machinecode.chainlink.spi.transport.WorkerId;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import org.infinispan.commands.remote.BaseRpcCommand;
import org.infinispan.context.InvocationContext;
import org.infinispan.remoting.transport.Address;

import java.util.UUID;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ExecuteCommand extends BaseRpcCommand implements ChainlinkCommand {

    public static final byte COMMAND_ID = 61;

    Address remote;
    WorkerId workerId;
    ExecutableEvent event;

    transient InfinispanTransport transport;

    public ExecuteCommand(final String cacheName) {
        super(cacheName);
    }

    public ExecuteCommand(final String cacheName, final Address remote, final WorkerId workerId, final ExecutableEvent event) {
        super(cacheName);
        this.remote = remote;
        this.workerId = workerId;
        this.event = event;
    }

    @Override
    public DeferredId perform(final InvocationContext invocationContext) throws Throwable {
        final Executable executable = event.getExecutable();
        final Address origin = getOrigin();
        final long jobExecutionId = executable.getContext().getJobExecutionId();
        final DeferredId deferredId = new UUIDDeferredId(UUID.randomUUID());
        final Deferred<?> deferred = new LinkedDeferred<Void>();
        transport.registerDeferred(jobExecutionId, deferredId, deferred);
        deferred.onResolve(new CommandListener(origin, new CompletionCommand(cacheName, origin, jobExecutionId, deferredId, Completion.RESOLVE), transport));
        deferred.onReject(new CommandListener(origin, new CompletionCommand(cacheName, origin, jobExecutionId, deferredId, Completion.REJECT), transport));
        deferred.onCancel(new CommandListener(origin, new CompletionCommand(cacheName, origin, jobExecutionId, deferredId, Completion.CANCEL), transport));
        transport.getWorker(workerId).addExecutable(event);
        return deferredId;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_ID;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{ remote, workerId, event };
    }

    @Override
    public void setTransport(final InfinispanTransport transport) {
        this.transport = transport;
    }


    @Override
    public void setParameters(final int commandId, final Object[] parameters) {
        if (commandId != getCommandId()) throw new IllegalStateException(); //TODO Message
        this.remote = (Address)parameters[0];
        this.workerId = (WorkerId)parameters[1];
        this.event = (ExecutableEvent)parameters[2];
    }

    @Override
    public boolean isReturnValueExpected() {
        return true;
    }
}
