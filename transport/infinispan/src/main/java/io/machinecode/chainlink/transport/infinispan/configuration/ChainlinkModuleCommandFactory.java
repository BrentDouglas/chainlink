package io.machinecode.chainlink.transport.infinispan.configuration;

import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.transport.infinispan.cmd.CleanupCommand;
import io.machinecode.chainlink.transport.infinispan.cmd.CompletionCommand;
import io.machinecode.chainlink.transport.infinispan.cmd.CreateDeferredCommand;
import io.machinecode.chainlink.transport.infinispan.cmd.ExecuteCommand;
import io.machinecode.chainlink.transport.infinispan.cmd.InvokeDeferredCommand;
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
            case ExecuteCommand.COMMAND_ID:
                command = new ExecuteCommand(cacheName);
                break;
            case CleanupCommand.COMMAND_ID:
                command = new CleanupCommand(cacheName);
                break;
            case CompletionCommand.COMMAND_ID:
                command = new CompletionCommand(cacheName);
                break;
            case InvokeDeferredCommand.COMMAND_ID:
                command = new InvokeDeferredCommand(cacheName);
                break;
            case InvokeExecutionRepositoryCommand.COMMAND_ID:
                command = new InvokeExecutionRepositoryCommand(cacheName);
                break;
            case CreateDeferredCommand.COMMAND_ID:
                command = new CreateDeferredCommand(cacheName);
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
        map.put(ExecuteCommand.COMMAND_ID, ExecuteCommand.class);
        map.put(CleanupCommand.COMMAND_ID, CleanupCommand.class);
        map.put(CompletionCommand.COMMAND_ID, CompletionCommand.class);
        map.put(InvokeDeferredCommand.COMMAND_ID, InvokeDeferredCommand.class);
        map.put(InvokeExecutionRepositoryCommand.COMMAND_ID, InvokeExecutionRepositoryCommand.class);
        map.put(CreateDeferredCommand.COMMAND_ID, CreateDeferredCommand.class);
        return map;
    }

    @Override
    public ReplicableCommand fromStream(final byte commandId, final Object[] args) {
        throw new IllegalStateException(); //TODO Message
    }
}
