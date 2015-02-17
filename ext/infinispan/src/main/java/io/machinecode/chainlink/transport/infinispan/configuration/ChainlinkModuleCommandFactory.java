package io.machinecode.chainlink.transport.infinispan.configuration;

import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.transport.infinispan.CommandAdapter;
import org.infinispan.commands.ReplicableCommand;
import org.infinispan.commands.module.ExtendedModuleCommandFactory;
import org.infinispan.commands.remote.CacheRpcCommand;

import java.util.Map;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ChainlinkModuleCommandFactory implements ExtendedModuleCommandFactory {
    @Override
    public CacheRpcCommand fromStream(final byte commandId, final Object[] args, final String cacheName) {
        final CacheRpcCommand command;
        switch (commandId) {
            case CommandAdapter.COMMAND_ID_61:
                command = new CommandAdapter(cacheName);
                break;
            default:
                throw new IllegalStateException(); //TODO Maybe
        }
        command.setParameters(commandId, args);
        return command;
    }

    @Override
    public Map<Byte, Class<? extends ReplicableCommand>> getModuleCommands() {
        final THashMap<Byte, Class<? extends ReplicableCommand>> map = new THashMap<>();
        map.put(CommandAdapter.COMMAND_ID_61, CommandAdapter.class);
        return map;
    }

    @Override
    public ReplicableCommand fromStream(final byte commandId, final Object[] args) {
        throw new IllegalStateException(); //TODO Message
    }
}
