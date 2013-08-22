package io.machinecode.nock.core.model;

import io.machinecode.nock.jsl.util.ForwardingList;
import io.machinecode.nock.spi.element.Properties;

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
    public List<PropertyImpl> getProperties() {
        return this.delegate;
    }

    @Override
    public String getPartition() {
        return this.partition;
    }
}
