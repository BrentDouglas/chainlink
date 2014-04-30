package io.machinecode.chainlink.core;

import io.machinecode.chainlink.core.management.StaticEnvironment;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.core.util.ResolvableService;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.factory.ConfigurationFactory;
import io.machinecode.chainlink.spi.exception.NoConfigurationWithIdException;
import io.machinecode.chainlink.spi.management.Environment;
import io.machinecode.chainlink.spi.util.Messages;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public final class Chainlink {

    public static Environment environment() {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        final List<Environment> environments;
        try {
            environments = new ResolvableService<Environment>(Constants.ENVIRONMENT, Environment.class).resolve(tccl);
        } catch (final Exception e) {
            throw new RuntimeException(Messages.get("CHAINLINK-031003.environment.exception"), e);
        }
        final Environment environment;
        if (environments.isEmpty()) {
            environment = new StaticEnvironment();
        } else {
            try {
                environment = environments.get(0);
            } catch (final Exception e) {
                throw new IllegalStateException(Messages.get("CHAINLINK-031003.environment.exception"), e);
            }
        }
        return environment;
    }

    public static Configuration configuration(final String id) throws NoConfigurationWithIdException {
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
        throw new NoConfigurationWithIdException(Messages.format("CHAINLINK-031004.no.configuration.with.id", id));
    }

    public static List<String> configurations() {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        final List<? extends ConfigurationFactory> factories;
        try {
            factories = new ResolvableService<ConfigurationFactory>(Constants.CONFIGURATION_FACTORY_CLASS, ConfigurationFactory.class)
                    .resolve(tccl);
        } catch (final Exception e) {
            throw new RuntimeException(Messages.get("CHAINLINK-031001.configuration.exception"), e);
        }
        final List<String> ids = new ArrayList<String>(factories.size());
        for (final ConfigurationFactory factory : factories) {
            ids.add(factory.getId());
        }
        return ids;
    }
}
