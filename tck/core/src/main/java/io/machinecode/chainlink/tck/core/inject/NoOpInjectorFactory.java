package io.machinecode.chainlink.tck.core.inject;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.InjectorFactory;
import io.machinecode.chainlink.spi.inject.Injector;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class NoOpInjectorFactory implements InjectorFactory {
    @Override
    public Injector produce(final Dependencies dependencies, final Properties properties) {
        return new NoOpInjector();
    }

    public static class NoOpInjector implements Injector {
        @Override
        public boolean inject(final Object bean) throws Exception {
            return false;
        }
    }
}
