package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.spi.configuration.ConfigurationLoader;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.ConfigurationLoaderFactory;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TestConfigurationLoaderFactory implements ConfigurationLoaderFactory {

    public static volatile boolean called = false;

    @Override
    public ConfigurationLoader produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
        called = true;
        return new TestConfigurationLoader();
    }
}
