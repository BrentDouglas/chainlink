package io.machinecode.chainlink.spi.inject;

import io.machinecode.chainlink.spi.loader.ArtifactLoader;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface InjectionContext {

    ClassLoader getClassLoader();

    ArtifactLoader getArtifactLoader();

    Injector getInjector();

    InjectablesProvider getProvider();
}
