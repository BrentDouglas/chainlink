package io.machinecode.nock.core.model;

import io.machinecode.nock.core.loader.TypedArtifactReference;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.PropertyReference;
import io.machinecode.nock.spi.transport.Transport;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PropertyReferenceImpl<T> implements PropertyReference {

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

    public T load(final Transport transport, final Context context) throws Exception {
        if (this._cached != null) {
            return this._cached;
        }
        final T that = this.ref.load(transport, context, this);
        this._cached = that;
        return that;
    }
}
