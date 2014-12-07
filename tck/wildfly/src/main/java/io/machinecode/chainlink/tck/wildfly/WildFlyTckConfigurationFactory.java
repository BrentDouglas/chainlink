package io.machinecode.chainlink.tck.wildfly;

import io.machinecode.chainlink.ee.wildfly.configuration.WildFlyConfiguration;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.configuration.ConfigurationBuilder;
import io.machinecode.chainlink.spi.configuration.factory.ConfigurationFactory;
import io.machinecode.chainlink.tck.core.TckConfigurator;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class WildFlyTckConfigurationFactory implements ConfigurationFactory {

    @Override
    public String getId() {
        return Constants.DEFAULT_CONFIGURATION;
    }

    @Override
    public ConfigurationBuilder<?> produce() throws Exception {
        return TckConfigurator.produce(new WildFlyConfiguration.Builder());
    }
}
