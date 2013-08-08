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
public class PartitionPropertyContext {

    private final List<MutablePair<String,String>> jobProperties;

    public PartitionPropertyContext() {
        this.jobProperties = new ArrayList<MutablePair<String, String>>();
    }

    public PartitionPropertyContext(final PartitionPropertyContext parent) {
        this.jobProperties = parent == null ? new ArrayList<MutablePair<String, String>>() : parent.jobProperties;
    }

    public void addProperties(final Properties properties) {
        for (final Property property : properties.getProperties()) {
            addProperty(property);
        }
    }

    public void addProperty(final Property property) {
        jobProperties.add(MutablePair.of(property.getName(), property.getValue()));
    }

    public List<MutablePair<String, String>> getProperties() {
        return Collections.unmodifiableList(jobProperties);
    }
}
