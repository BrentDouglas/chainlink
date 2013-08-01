package io.machinecode.nock.jsl.impl;

import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.Property;
import io.machinecode.nock.jsl.impl.Util.Transformer;

import java.util.Collections;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PropertiesImpl implements Properties {

    private static final Transformer<Property> PROPERTY_TRANSFORMER = new Transformer<Property>() {
        @Override
        public Property transform(final Property that) {
            return new PropertyImpl(that);
        }
    };

    private final String partition;
    private final List<Property> properties;

    public PropertiesImpl(final Properties that) {
        if (that == null) {
            this.partition = null;
            this.properties = Collections.emptyList();
        } else  {
            this.partition = that.getPartition();
            this.properties = Util.immutableCopy(that.getProperties(), PROPERTY_TRANSFORMER);
        }
    }

    @Override
    public List<Property> getProperties() {
        return this.properties;
    }

    @Override
    public String getPartition() {
        return this.partition;
    }
}
