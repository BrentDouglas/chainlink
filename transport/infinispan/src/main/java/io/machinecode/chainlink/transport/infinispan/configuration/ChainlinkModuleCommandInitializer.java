package io.machinecode.chainlink.transport.infinispan.configuration;

import io.machinecode.chainlink.transport.infinispan.InfinispanTransport;
import org.infinispan.commands.ReplicableCommand;
import org.infinispan.commands.module.ModuleCommandInitializer;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ChainlinkModuleCommandInitializer implements ModuleCommandInitializer {

    private InfinispanTransport transport;

    public void setTransport(final InfinispanTransport transport) {
        this.transport = transport;
    }

    @Override
    public void initializeReplicableCommand(final ReplicableCommand command, final boolean isRemote) {
        if (command instanceof ChainlinkCommand) {
            ((ChainlinkCommand)command).setTransport(transport);
        }
    }
}
