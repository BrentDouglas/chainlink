package io.machinecode.nock.jsl.impl;

import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.PropertyReference;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PropertyReferenceImpl implements PropertyReference {

    private final String ref;
    private final Properties properties;

    public PropertyReferenceImpl(final PropertyReference that) {
        this.ref = that.getRef();
        this.properties = new PropertiesImpl(that.getProperties());
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
