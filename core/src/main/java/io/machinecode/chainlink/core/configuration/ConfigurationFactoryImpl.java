package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.core.util.ResolvableService;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.ConfigurationFactory;
import io.machinecode.chainlink.spi.util.Messages;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ConfigurationFactoryImpl implements ConfigurationFactory {

    public static final ConfigurationFactoryImpl INSTANCE = new ConfigurationFactoryImpl();

    private Configuration configuration;

    @Override
    public synchronized Configuration produce() {
        if (this.configuration != null) {
            return this.configuration;
        }
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        final List<ConfigurationFactory> factories;
        try {
            factories = new ResolvableService<ConfigurationFactory>(ConfigurationFactory.class).resolve(tccl);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (factories.isEmpty()) {
            throw new IllegalStateException(Messages.get("CHAINLINK-031000.configuration.not.provided"));
        } else {
            try {
                this.configuration = factories.get(0).produce();
            } catch (final Exception e) {
                throw new IllegalStateException(Messages.get("CHAINLINK-031001.configuration.exception"), e);
            }
        }
        return this.configuration;
    }
}
