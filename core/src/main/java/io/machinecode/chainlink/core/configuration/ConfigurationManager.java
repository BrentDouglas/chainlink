package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.core.util.ResolvableService;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.factory.ConfigurationFactory;
import io.machinecode.chainlink.spi.util.Messages;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public final class ConfigurationManager {

    public static synchronized Configuration loadConfiguration() {
        final String factoryClassName = AccessController.doPrivileged(new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty(Constants.CONFIGURATION_FACTORY_CLASS);
            }
        });
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        if (factoryClassName != null) {
            try {
                return ((ConfigurationFactory)tccl.loadClass(factoryClassName).newInstance()).produce();
            } catch (final Exception e) {
                throw new RuntimeException(Messages.get("CHAINLINK-031001.configuration.exception"), e);
            }
        }
        final List<ConfigurationFactory> factories;
        try {
            factories = new ResolvableService<ConfigurationFactory>(ConfigurationFactory.class).resolve(tccl);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(Messages.get("CHAINLINK-031001.configuration.exception"), e);
        }
        if (factories.isEmpty()) {
            throw new IllegalStateException(Messages.get("CHAINLINK-031000.configuration.not.provided"));
        } else {
            try {
                return factories.get(0).produce();
            } catch (final Exception e) {
                throw new IllegalStateException(Messages.get("CHAINLINK-031001.configuration.exception"), e);
            }
        }
    }
}
