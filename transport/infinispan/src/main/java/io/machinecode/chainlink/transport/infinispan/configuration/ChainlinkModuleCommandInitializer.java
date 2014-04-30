package io.machinecode.chainlink.transport.infinispan.configuration;

import io.machinecode.chainlink.transport.infinispan.InfinispanRegistry;
import org.infinispan.commands.ReplicableCommand;
import org.infinispan.commands.module.ModuleCommandInitializer;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ChainlinkModuleCommandInitializer implements ModuleCommandInitializer {

    private InfinispanRegistry registry;

    public void init(final InfinispanRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void initializeReplicableCommand(final ReplicableCommand command, final boolean isRemote) {
        if (command instanceof ChainlinkCommand) {
            ((ChainlinkCommand)command).init(registry);
        }
    }
}
