package io.machinecode.nock.jsl.fluent;

import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.Property;
import io.machinecode.nock.jsl.util.ForwardingList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentProperties<T extends FluentProperties<T>> extends ForwardingList<Property> implements Properties {

    private String partition;
    //private final List<Property> properties = new ArrayList<Property>(0);

    public FluentProperties() {
        super(new ArrayList<Property>());
    }

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
        return this.delegate;
    }

    public T addProperty(final Property property) {
        this.delegate.add(property);
        return (T) this;
    }

    public T addProperty(final String name, final String value) {
        this.delegate.add(new FluentProperty().setName(name).setValue(value));
        return (T) this;
    }
}
