package io.machinecode.nock.core.model;

import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.spi.element.PropertyReference;
import io.machinecode.nock.spi.inject.Resolvable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PropertyReferenceImpl<T> implements PropertyReference, Resolvable<T> {

    private final PropertiesImpl properties;
    private final ResolvableReference<T> ref;

    public PropertyReferenceImpl(final ResolvableReference<T> ref, final PropertiesImpl properties) {
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
    public T resolve(final ClassLoader loader) throws Exception {
        return this.ref.resolve(loader);
    }
}
