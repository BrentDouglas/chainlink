package io.machinecode.nock.core.descriptor;

import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.PropertyReference;

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
