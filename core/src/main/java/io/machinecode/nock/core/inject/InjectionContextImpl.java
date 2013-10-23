package io.machinecode.nock.core.inject;

import io.machinecode.nock.core.loader.ArtifactLoaderImpl;
import io.machinecode.nock.spi.configuration.Configuration;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.inject.Injector;
import io.machinecode.nock.spi.loader.ArtifactLoader;

import java.lang.ref.WeakReference;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InjectionContextImpl implements InjectionContext {

    private final WeakReference<ClassLoader> classLoader;
    private final ArtifactLoader artifactLoader;
    private final Injector injector;

    public InjectionContextImpl(final Configuration configuration) {
        this.classLoader = new WeakReference<ClassLoader>(configuration.getClassLoader());
        this.artifactLoader = new ArtifactLoaderImpl(configuration);
        this.injector = new InjectorImpl(configuration);
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.classLoader.get();
    }

    @Override
    public ArtifactLoader getArtifactLoader() {
        return this.artifactLoader;
    }

    @Override
    public Injector getInjector() {
        return injector;
    }
}
