package io.machinecode.chainlink.tck.se;

import io.machinecode.chainlink.se.configuration.SeConfiguration;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.configuration.ConfigurationBuilder;
import io.machinecode.chainlink.spi.configuration.factory.ConfigurationFactory;
import io.machinecode.chainlink.tck.core.TckConfigurator;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class SeTckConfigurationFactory implements ConfigurationFactory {

    @Override
    public String getId() {
        return Constants.DEFAULT_CONFIGURATION;
    }

    @Override
    public ConfigurationBuilder<?> produce(final ClassLoader loader) throws Exception {
        return TckConfigurator.produce(new SeConfiguration.Builder(), loader);
    }
}
