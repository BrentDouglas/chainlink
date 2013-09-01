package io.machinecode.nock.core.expression;

import io.machinecode.nock.spi.factory.PropertyContext;
import io.machinecode.nock.spi.element.Property;
import io.machinecode.nock.jsl.util.MutablePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class PropertyContextImpl implements PropertyContext {

    private final List<MutablePair<String,String>> properties;

    public PropertyContextImpl() {
        this.properties = new ArrayList<MutablePair<String, String>>();
    }

    public PropertyContextImpl(final Properties properties) {
        this();
        addProperties(properties);
    }

    public PropertyContextImpl(final PropertyContextImpl parent) {
        this.properties = parent == null ? new ArrayList<MutablePair<String, String>>() : parent.properties;
    }

    public void addProperties(final Properties properties) {
        for (final Entry<Object, Object> entry : properties.entrySet()) {
            final Object key = entry.getKey();
            final Object value = entry.getValue();
            this.properties.add(MutablePair.of(
                    key instanceof String ? (String) key : key.toString(),
                    value instanceof String ? (String) value : value.toString()
            ));
        }
    }

    public void addProperty(final Property property) {
        properties.add(MutablePair.of(property.getName(), property.getValue()));
    }

    @Override
    public List<MutablePair<String, String>> getProperties() {
        return Collections.unmodifiableList(properties);
    }
}
