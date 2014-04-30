package io.machinecode.chainlink.core.registry;

import io.machinecode.chainlink.spi.configuration.RegistryConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.RegistryFactory;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class LocalRegistryFactory implements RegistryFactory {
    @Override
    public LocalRegistry produce(final RegistryConfiguration configuration) throws Exception {
        return new LocalRegistry();
    }
}
