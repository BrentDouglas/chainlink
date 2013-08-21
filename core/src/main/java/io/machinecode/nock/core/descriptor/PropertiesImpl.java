package io.machinecode.nock.core.descriptor;

import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.Property;
import io.machinecode.nock.jsl.util.ForwardingList;

import java.util.Collections;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PropertiesImpl extends ForwardingList<PropertyImpl> implements Properties {

    private final String partition;

    public PropertiesImpl(final String partition, final List<PropertyImpl> properties) {
        super(properties == null
                ? Collections.<PropertyImpl>emptyList()
                : properties
        );
        this.partition = partition;
    }

    @Override
    public List<? extends Property> getProperties() {
        return this.delegate;
    }

    @Override
    public String getPartition() {
        return this.partition;
    }
}
