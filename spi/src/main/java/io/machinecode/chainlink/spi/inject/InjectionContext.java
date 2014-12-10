package io.machinecode.chainlink.spi.inject;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface InjectionContext {

    ClassLoader getClassLoader();

    ArtifactLoader getArtifactLoader();

    Injector getInjector();

    InjectablesProvider getProvider();
}
