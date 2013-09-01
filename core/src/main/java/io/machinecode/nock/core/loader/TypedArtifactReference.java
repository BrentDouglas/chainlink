package io.machinecode.nock.core.loader;

import io.machinecode.nock.core.util.ResolvableClass;
import io.machinecode.nock.spi.loader.ArtifactLoader;

import javax.batch.operations.BatchRuntimeException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class TypedArtifactReference<T> {

    private final String ref;
    private final ResolvableClass<T> clazz;
    private transient Class<T> _clazz;

    public TypedArtifactReference(final String ref, final Class<T> clazz) {
        this.ref = ref;
        this.clazz = new ResolvableClass<T>(clazz);
    }

    public T load(final ClassLoader classLoader, final ArtifactLoader artifactLoader) throws BatchRuntimeException {
        return artifactLoader.load(this.ref, this.type(classLoader), classLoader);
    }

    public String ref() {
        return this.ref;
    }

    public Class<T> type(final ClassLoader classLoader) {
        if (this._clazz == null) {
            try {
                this._clazz = this.clazz.resolve(classLoader);
            } catch (final ClassNotFoundException e) {
                throw new BatchRuntimeException(e);
            }
        }
        return this._clazz;
    }
}
