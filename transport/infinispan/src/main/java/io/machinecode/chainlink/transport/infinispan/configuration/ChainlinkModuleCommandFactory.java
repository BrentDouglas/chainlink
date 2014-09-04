package io.machinecode.chainlink.transport.infinispan.configuration;

import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.transport.infinispan.cmd.CleanupCommand;
import io.machinecode.chainlink.transport.infinispan.cmd.PushChainCommand;
import io.machinecode.chainlink.transport.infinispan.cmd.ExecuteCommand;
import io.machinecode.chainlink.transport.infinispan.cmd.InvokeChainCommand;
import io.machinecode.chainlink.transport.infinispan.cmd.InvokeExecutionRepositoryCommand;
import org.infinispan.commands.ReplicableCommand;
import org.infinispan.commands.module.ExtendedModuleCommandFactory;
import org.infinispan.commands.remote.CacheRpcCommand;

import java.util.Map;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ChainlinkModuleCommandFactory implements ExtendedModuleCommandFactory {
    @Override
    public CacheRpcCommand fromStream(final byte commandId, final Object[] args, final String cacheName) {
        final CacheRpcCommand command;
        switch (commandId) {
            case ExecuteCommand.COMMAND_ID_61:
                command = new ExecuteCommand(cacheName);
                break;
            case CleanupCommand.COMMAND_ID_62:
                command = new CleanupCommand(cacheName);
                break;
            case InvokeExecutionRepositoryCommand.COMMAND_ID_63:
                command = new InvokeExecutionRepositoryCommand(cacheName);
                break;
            case InvokeChainCommand.COMMAND_ID_64:
                command = new InvokeChainCommand(cacheName);
                break;
            case PushChainCommand.COMMAND_ID_65:
                command = new PushChainCommand(cacheName);
                break;
            default:
                throw new IllegalStateException(); //TODO Maybe
        }
        command.setParameters(commandId, args);
        return command;
    }

    @Override
    public Map<Byte, Class<? extends ReplicableCommand>> getModuleCommands() {
        final THashMap<Byte, Class<? extends ReplicableCommand>> map = new THashMap<Byte, Class<? extends ReplicableCommand>>();
        map.put(ExecuteCommand.COMMAND_ID_61, ExecuteCommand.class);
        map.put(CleanupCommand.COMMAND_ID_62, CleanupCommand.class);
        map.put(InvokeExecutionRepositoryCommand.COMMAND_ID_63, InvokeExecutionRepositoryCommand.class);
        map.put(InvokeChainCommand.COMMAND_ID_64, InvokeChainCommand.class);
        map.put(PushChainCommand.COMMAND_ID_65, PushChainCommand.class);
        return map;
    }

    @Override
    public ReplicableCommand fromStream(final byte commandId, final Object[] args) {
        throw new IllegalStateException(); //TODO Message
    }
}
