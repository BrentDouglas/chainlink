package io.machinecode.chainlink.tck.core.inject;

import io.machinecode.chainlink.inject.core.VetoInjector;
import io.machinecode.chainlink.spi.configuration.LoaderConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.InjectorFactory;
import io.machinecode.chainlink.spi.inject.Injector;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class VetoInjectorFactory implements InjectorFactory {
    @Override
    public Injector produce(final LoaderConfiguration configuration) {
        return new VetoInjector();
    }
}
