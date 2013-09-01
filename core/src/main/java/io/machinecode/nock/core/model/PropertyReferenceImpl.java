package io.machinecode.nock.core.model;

import io.machinecode.nock.core.loader.TypedArtifactReference;
import io.machinecode.nock.spi.element.PropertyReference;
import io.machinecode.nock.spi.inject.InjectableArtifact;
import io.machinecode.nock.spi.inject.InjectionContext;

import javax.batch.operations.BatchRuntimeException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PropertyReferenceImpl<T> implements PropertyReference, InjectableArtifact<T> {

    protected final PropertiesImpl properties;
    protected final TypedArtifactReference<T> ref;
    protected transient T _cached;

    public PropertyReferenceImpl(final TypedArtifactReference<T> ref, final PropertiesImpl properties) {
        this.ref = ref;
        this.properties = properties;
    }

    @Override
    public String getRef() {
        return this.ref.ref();
    }

    @Override
    public PropertiesImpl getProperties() {
        return this.properties;
    }

    @Override
    public T load(final InjectionContext context) throws BatchRuntimeException {
        if (this._cached != null) {
            return this._cached;
        }
        final ClassLoader classLoader = context.getClassLoader();
        final T that = this.ref.load(classLoader, context.getArtifactLoader());
        this._cached = that;
        return that;
    }
}
