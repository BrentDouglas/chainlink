package io.machinecode.nock.core.expression;

import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.Property;
import io.machinecode.nock.jsl.util.MutablePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class PartitionPropertyContext implements PropertyContext {

    private final List<MutablePair<String,String>> properties;

    public PartitionPropertyContext() {
        this.properties = new ArrayList<MutablePair<String, String>>();
    }

    public PartitionPropertyContext(final PartitionPropertyContext parent) {
        this.properties = parent == null ? new ArrayList<MutablePair<String, String>>() : parent.properties;
    }

    public void addProperties(final Properties properties) {
        for (final Property property : properties.getProperties()) {
            addProperty(property);
        }
    }

    public void addProperty(final Property property) {
        properties.add(MutablePair.of(property.getName(), property.getValue()));
    }

    public List<MutablePair<String, String>> getProperties() {
        return Collections.unmodifiableList(properties);
    }
}
