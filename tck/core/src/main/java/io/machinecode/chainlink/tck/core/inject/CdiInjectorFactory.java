package io.machinecode.chainlink.tck.core.inject;

import io.machinecode.chainlink.inject.cdi.CdiArtifactLoader;
import io.machinecode.chainlink.inject.cdi.CdiInjector;
import io.machinecode.chainlink.spi.configuration.LoaderConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.InjectorFactory;
import io.machinecode.chainlink.spi.inject.Injector;
import io.machinecode.chainlink.tck.core.loader.CdiArtifactLoaderFactory;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class CdiInjectorFactory implements InjectorFactory {
    @Override
    public Injector produce(final LoaderConfiguration configuration) {
        return CdiArtifactLoader.inject(CdiArtifactLoaderFactory.container.getBeanManager(), CdiInjector.class);
    }
}
