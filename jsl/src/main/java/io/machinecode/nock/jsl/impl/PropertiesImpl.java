package io.machinecode.nock.jsl.impl;

import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PropertiesImpl implements Properties {

    private final List<Property> properties;

    public PropertiesImpl(final Properties that) {
        this.properties = new ArrayList<Property>(that.getProperties().size());
        for (final Property property : that.getProperties()) {
            this.properties.add(new PropertyImpl(property));
        }
    }

    @Override
    public List<Property> getProperties() {
        return this.properties;
    }
}
