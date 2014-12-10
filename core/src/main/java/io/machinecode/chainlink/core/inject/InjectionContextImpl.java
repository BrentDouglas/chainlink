package io.machinecode.chainlink.core.inject;

import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.inject.Injector;

import java.lang.ref.WeakReference;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class InjectionContextImpl implements InjectionContext {

    private final WeakReference<ClassLoader> classLoader;
    private final ArtifactLoader artifactLoader;
    private final Injector injector;
    private final InjectablesProvider provider;

    public InjectionContextImpl(final ClassLoader classLoader, final ArtifactLoader artifactLoader, final Injector injector) {
        this.classLoader = new WeakReference<ClassLoader>(classLoader);
        this.artifactLoader = artifactLoader;
        this.injector = injector;
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
