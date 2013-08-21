package io.machinecode.nock.core.expression;

import io.machinecode.nock.jsl.util.MutablePair;
import io.machinecode.nock.spi.element.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class JobPropertyContext implements PropertyContext {

    private final List<MutablePair<String,String>> properties;

    public JobPropertyContext() {
        this.properties = new ArrayList<MutablePair<String, String>>();
    }

    public void addProperty(final Property property) {
        this.properties.add(MutablePair.of(property.getName(), property.getValue()));
    }

    @Override
    public List<MutablePair<String, String>> getProperties() {
        return Collections.unmodifiableList(this.properties);
    }
}
