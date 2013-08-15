package io.machinecode.nock.core.expression;

import io.machinecode.nock.jsl.api.Property;
import io.machinecode.nock.jsl.util.MutablePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class JobPropertyContext implements PropertyContext {

    private final List<MutablePair<String,String>> properties;
    private final List<MutablePair<String,String>> parameters;

    public JobPropertyContext(final java.util.Properties parameters) {
        this.properties = new ArrayList<MutablePair<String, String>>();
        this.parameters = new ArrayList<MutablePair<String, String>>();
        for (final Entry<Object, Object> entry : parameters.entrySet()) {
            final Object key = entry.getKey();
            final Object value = entry.getValue();
            this.parameters.add(MutablePair.of(
                    key instanceof String ? (String)key : key.toString(),
                    value instanceof String ? (String)value : value.toString()
            ));
        }
    }

    public void addProperty(final Property property) {
        this.properties.add(MutablePair.of(property.getName(), property.getValue()));
    }

    public List<MutablePair<String, String>> getProperties() {
        return Collections.unmodifiableList(this.properties);
    }

    public List<MutablePair<String, String>> getParameters() {
        return this.parameters;
    }
}
