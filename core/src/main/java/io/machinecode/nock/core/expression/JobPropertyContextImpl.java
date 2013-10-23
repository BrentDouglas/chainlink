package io.machinecode.nock.core.expression;

import io.machinecode.nock.jsl.util.MutablePair;
import io.machinecode.nock.spi.factory.JobPropertyContext;
import io.machinecode.nock.spi.element.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class JobPropertyContextImpl implements JobPropertyContext {

    private final List<MutablePair<String,String>> properties;
    private final List<MutablePair<String,String>> parameters;

    public JobPropertyContextImpl(final java.util.Properties parameters) {
        this.properties = new ArrayList<MutablePair<String, String>>();
        this.parameters = new ArrayList<MutablePair<String, String>>();
        if (parameters == null) {
            return;
        }
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

    @Override
    public List<MutablePair<String, String>> getProperties() {
        return Collections.unmodifiableList(this.properties);
    }

    @Override
    public List<MutablePair<String, String>> getParameters() {
        return this.parameters;
    }
}
