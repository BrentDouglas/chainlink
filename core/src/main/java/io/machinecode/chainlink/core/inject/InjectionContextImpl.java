package io.machinecode.chainlink.core.inject;

import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.inject.Injector;
import io.machinecode.chainlink.spi.loader.ArtifactLoader;

import java.lang.ref.WeakReference;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InjectionContextImpl implements InjectionContext {

    private final WeakReference<ClassLoader> classLoader;
    private final ArtifactLoader artifactLoader;
    private final Injector injector;
    private final InjectablesProvider provider;

    public InjectionContextImpl(final RuntimeConfiguration configuration) {
        this.classLoader = new WeakReference<ClassLoader>(configuration.getClassLoader());
        this.artifactLoader = configuration.getArtifactLoader();
        this.injector = configuration.getInjector();
        this.provider = new InjectablesProviderImpl();
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

    @Override
    public InjectablesProvider getProvider() {
        return provider;
    }
}
