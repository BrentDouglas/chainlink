package io.machinecode.nock.core.model;

import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.PropertyReference;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PropertyReferenceImpl implements PropertyReference {

    private final String ref;
    private final Properties properties;

    public PropertyReferenceImpl(final String ref, final Properties properties) {
        this.ref = ref;
        this.properties = properties;
    }

    @Override
    public String getRef() {
        return this.ref;
    }

    @Override
    public Properties getProperties() {
        return this.properties;
    }
}
