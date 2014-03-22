package io.machinecode.chainlink.execution.infinispan.cmd;

import io.machinecode.chainlink.spi.deferred.Deferred;
import org.infinispan.context.InvocationContext;
import org.infinispan.remoting.transport.Address;

import java.util.UUID;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ResolveCommand extends BaseCommand {

    public static final byte COMMAND_ID = 64;

    public ResolveCommand(final String cacheName, final Address remote, final UUID uuid) {
        super(cacheName, remote, uuid);
    }

    @Override
    public Object perform(final InvocationContext invocationContext) throws Throwable {
        final Deferred<?> deferred = cache.get(remote).unregisterDeferred(uuid);
        deferred.resolve(null);
        return true;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_ID;
    }
}
