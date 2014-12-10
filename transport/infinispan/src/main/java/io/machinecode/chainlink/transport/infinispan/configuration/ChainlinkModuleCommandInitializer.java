package io.machinecode.chainlink.transport.infinispan.configuration;

import io.machinecode.chainlink.transport.infinispan.InfinispanTransport;
import org.infinispan.commands.ReplicableCommand;
import org.infinispan.commands.module.ModuleCommandInitializer;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ChainlinkModuleCommandInitializer implements ModuleCommandInitializer {

    private InfinispanTransport transport;

    public void init(final InfinispanTransport transport) {
        this.transport = transport;
    }

    @Override
    public void initializeReplicableCommand(final ReplicableCommand command, final boolean isRemote) {
        if (command instanceof ChainlinkCommand) {
            ((ChainlinkCommand)command).init(transport);
        }
    }
}
