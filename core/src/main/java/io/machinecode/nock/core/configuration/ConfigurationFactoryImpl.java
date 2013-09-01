package io.machinecode.nock.core.configuration;

import io.machinecode.nock.core.util.ResolvableService;
import io.machinecode.nock.spi.configuration.Configuration;
import io.machinecode.nock.spi.configuration.ConfigurationFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ConfigurationFactoryImpl implements ConfigurationFactory {

    public static final ConfigurationFactoryImpl INSTANCE = new ConfigurationFactoryImpl();

    private Configuration configuration;
    final AtomicBoolean lock = new AtomicBoolean(false);

    @Override
    public Configuration produce() {
        while (!lock.compareAndSet(false, true)) {}
        try {
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
                this.configuration = new ConfigurationImpl(tccl);
            } else {
                this.configuration = factories.get(0).produce();
            }
            return this.configuration;
        } finally {
            lock.set(false);
        }
    }
}
