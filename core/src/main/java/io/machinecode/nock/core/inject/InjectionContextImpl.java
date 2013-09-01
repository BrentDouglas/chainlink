package io.machinecode.nock.core.inject;

import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.loader.ArtifactLoader;

import java.lang.ref.WeakReference;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InjectionContextImpl implements InjectionContext {

    private final WeakReference<ClassLoader> classLoader;
    private final ArtifactLoader artifactLoader;

    public InjectionContextImpl(final ClassLoader classLoader, final ArtifactLoader artifactLoader) {
        this.classLoader = new WeakReference<ClassLoader>(classLoader);
        this.artifactLoader = artifactLoader;
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.classLoader.get();
    }

    @Override
    public ArtifactLoader getArtifactLoader() {
        return this.artifactLoader;
    }
}
