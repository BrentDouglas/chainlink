package io.machinecode.chainlink.inject.cdi;

import io.machinecode.chainlink.spi.configuration.ConfigurationLoader;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.ConfigurationLoaderFactory;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class CdiConfigurationLoaderFactory implements ConfigurationLoaderFactory, Extension {

    private final BeanManager beanManager;

    public CdiConfigurationLoaderFactory(final BeanManager manager) {
        beanManager = manager;
    }

    public CdiConfigurationLoaderFactory() {
        this(CdiExtension.beanManager);
    }

    @Override
    public ConfigurationLoader produce(final Dependencies dependencies, final Properties properties) throws Exception {
        return new CdiConfigurationLoader(beanManager);
    }
}
