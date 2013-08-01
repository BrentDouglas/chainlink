package io.machinecode.nock.jsl.fluent;

import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentProperties<T extends FluentProperties<T>> implements Properties {

    private String partition;
    private final List<Property> properties = new ArrayList<Property>(0);

    @Override
    public String getPartition() {
        return partition;
    }

    public T setPartition(final String partition) {
        this.partition = partition;
        return (T)this;
    }

    @Override
    public List<Property> getProperties() {
        return this.properties;
    }

    public T addProperty(final Property property) {
        this.properties.add(property);
        return (T) this;
    }

    public T addProperty(final String name, final String value) {
        this.properties.add(new FluentProperty().setName(name).setValue(value));
        return (T) this;
    }
}
