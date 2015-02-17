package io.machinecode.chainlink.transport.infinispan.configuration;

import io.machinecode.chainlink.spi.configuration.Configuration;
import org.infinispan.commands.ReplicableCommand;
import org.infinispan.commands.module.ModuleCommandInitializer;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ChainlinkModuleCommandInitializer implements ModuleCommandInitializer {

    private Configuration configuration;

    public void init(final Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void initializeReplicableCommand(final ReplicableCommand command, final boolean isRemote) {
        if (command instanceof ChainlinkCommand) {
            ((ChainlinkCommand)command).init(configuration);
        }
    }
}
