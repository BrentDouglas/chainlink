package io.machinecode.chainlink.spi.inject;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface InjectionContext {

    ClassLoader getClassLoader();

    ArtifactLoader getArtifactLoader();

    Injector getInjector();

    InjectablesProvider getProvider();
}
