package io.machinecode.chainlink.core.registry;

import io.machinecode.chainlink.spi.configuration.RegistryConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.RegistryFactory;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class LocalRegistryFactory implements RegistryFactory {
    @Override
    public LocalRegistry produce(final RegistryConfiguration configuration) throws Exception {
        return new LocalRegistry();
    }
}
