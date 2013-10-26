package io.machinecode.nock.spi.inject;

import io.machinecode.nock.spi.loader.ArtifactLoader;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface InjectionContext {

    ClassLoader getClassLoader();

    ArtifactLoader getArtifactLoader();

    Injector getInjector();

    InjectablesProvider getProvider();
}
