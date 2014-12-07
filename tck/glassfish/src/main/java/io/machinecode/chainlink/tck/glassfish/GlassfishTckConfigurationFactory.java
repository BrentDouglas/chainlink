package io.machinecode.chainlink.tck.glassfish;

import io.machinecode.chainlink.ee.glassfish.GlassfishConfigutation;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.configuration.ConfigurationBuilder;
import io.machinecode.chainlink.spi.configuration.factory.ConfigurationFactory;
import io.machinecode.chainlink.tck.core.TckConfigurator;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GlassfishTckConfigurationFactory implements ConfigurationFactory {

    @Override
    public String getId() {
        return Constants.DEFAULT_CONFIGURATION;
    }

    @Override
    public ConfigurationBuilder<?> produce() throws Exception {
        return TckConfigurator.produce(new GlassfishConfigutation.Builder());
    }
}
