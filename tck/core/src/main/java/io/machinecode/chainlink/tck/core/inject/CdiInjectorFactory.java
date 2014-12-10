package io.machinecode.chainlink.tck.core.inject;

import io.machinecode.chainlink.inject.cdi.CdiArtifactLoader;
import io.machinecode.chainlink.inject.cdi.CdiInjector;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.InjectorFactory;
import io.machinecode.chainlink.spi.inject.Injector;
import io.machinecode.chainlink.tck.core.loader.CdiArtifactLoaderFactory;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class CdiInjectorFactory implements InjectorFactory {
    @Override
    public Injector produce(final Dependencies dependencies, final Properties properties) {
        return CdiArtifactLoader.inject(CdiArtifactLoaderFactory.container.getBeanManager(), CdiInjector.class);
    }
}
