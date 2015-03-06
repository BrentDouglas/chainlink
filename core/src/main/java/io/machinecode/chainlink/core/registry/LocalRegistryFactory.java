package io.machinecode.chainlink.core.registry;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.RegistryFactory;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class LocalRegistryFactory implements RegistryFactory {
    @Override
    public LocalRegistry produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
        return new LocalRegistry();
    }
}
