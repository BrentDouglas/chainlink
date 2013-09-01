package io.machinecode.nock.core.loader;

import io.machinecode.nock.spi.loader.ArtifactLoader;

import javax.batch.operations.BatchRuntimeException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ArtifactReference {

    private final String ref;

    public ArtifactReference(final String ref) {
        this.ref = ref;
    }

    public <T> T load(final ClassLoader classLoader, final Class<T> clazz, final ArtifactLoader artifactLoader) throws BatchRuntimeException {
        return artifactLoader.load(this.ref, clazz, classLoader);
    }

    public String ref() {
        return this.ref;
    }
}
