package io.machinecode.chainlink.execution.infinispan.cmd;

import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import org.infinispan.context.InvocationContext;
import org.infinispan.remoting.transport.Address;

import java.util.UUID;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CompletionCommand extends BaseCommand {

    public static final byte COMMAND_ID = 60;

    Completion completion;

    public CompletionCommand(final String cacheName, final Address remote, final UUID uuid, final Completion completion) {
        super(cacheName, remote, uuid);
        this.completion = completion;
    }

    @Override
    public Object perform(final InvocationContext invocationContext) throws Throwable {
        final Deferred<?> deferred = cache.get(remote).unregisterDeferred(uuid);
        switch (completion) {
            case RESOLVE:
                deferred.resolve(null);
                break;
            case REJECT:
                deferred.reject(null);
                break;
            case CANCEL:
                deferred.cancel(true);
                break;
        }
        return true;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_ID;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{ cacheName, remote, uuid, completion };
    }

    @Override
    public void setParameters(final int commandId, final Object[] parameters) {
        super.setParameters(commandId, parameters);
        this.completion = (Completion)parameters[3];
    }
}
