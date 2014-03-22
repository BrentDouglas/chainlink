package io.machinecode.chainlink.execution.infinispan.cmd;

import io.machinecode.chainlink.execution.infinispan.CommandListener;
import io.machinecode.chainlink.execution.infinispan.InfinispanExecutor;
import io.machinecode.chainlink.spi.context.ThreadId;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import org.infinispan.context.InvocationContext;
import org.infinispan.remoting.transport.Address;

import java.util.UUID;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ExecuteCommand extends BaseCommand {

    public static final byte COMMAND_ID = 61;

    ThreadId threadId;
    ExecutableEvent event;

    public ExecuteCommand(final String cacheName, final Address remote, final UUID uuid,
                          final ThreadId threadId, final ExecutableEvent event) {
        super(cacheName, remote, uuid);
        this.threadId = threadId;
        this.event = event;
    }

    @Override
    public Object perform(final InvocationContext invocationContext) throws Throwable {
        final InfinispanExecutor executor = cache.get(remote);
        final Executable executable = event.getExecutable();
        executable.onResolve(new CommandListener(origin, new ResolveCommand(cacheName, origin, uuid), executor));
        executable.onReject(new CommandListener(origin, new RejectCommand(cacheName, origin, uuid), executor));
        executable.onCancel(new CommandListener(origin, new CancelCommand(cacheName, origin, uuid), executor));
        executor.getWorker(threadId).addExecutable(event);
        return true;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_ID;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{ cacheName, remote, uuid, threadId, event, uuid };
    }

    @Override
    public void setParameters(final int commandId, final Object[] parameters) {
        super.setParameters(commandId, parameters);
        this.event = (ExecutableEvent)parameters[3];
        this.uuid = (UUID)parameters[4];
    }
}
