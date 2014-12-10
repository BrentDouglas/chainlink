package io.machinecode.chainlink.core.registry;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.RegistryFactory;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class LocalRegistryFactory implements RegistryFactory {
    @Override
    public LocalRegistry produce(final Dependencies dependencies, final Properties properties) throws Exception {
        return new LocalRegistry();
    }
}
