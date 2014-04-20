package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.core.util.ResolvableService;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.factory.ConfigurationFactory;
import io.machinecode.chainlink.spi.exception.NoConfigurationWithIdException;
import io.machinecode.chainlink.spi.util.Messages;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public final class ConfigurationManager {

    public static synchronized Configuration loadConfiguration(final String id) throws NoConfigurationWithIdException {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        final List<ConfigurationFactory> factories;
        try {
            factories = new ResolvableService<ConfigurationFactory>(Constants.CONFIGURATION_FACTORY_CLASS, ConfigurationFactory.class)
                    .resolve(tccl);
        } catch (final Exception e) {
            throw new RuntimeException(Messages.get("CHAINLINK-031001.configuration.exception"), e);
        }
        if (factories.isEmpty()) {
            throw new IllegalStateException(Messages.get("CHAINLINK-031000.configuration.not.provided"));
        } else {
            try {
                for (final ConfigurationFactory factory : factories) {
                    if (id.equals(factory.getId())) {
                        return factory.produce();
                    }
                }
            } catch (final Exception e) {
                throw new IllegalStateException(Messages.get("CHAINLINK-031001.configuration.exception"), e);
            }
        }
        throw new NoConfigurationWithIdException(Messages.get("")); //TODO Message
    }
}
