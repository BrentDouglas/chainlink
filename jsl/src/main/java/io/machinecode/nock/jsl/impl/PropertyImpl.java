package io.machinecode.nock.jsl.impl;

import io.machinecode.nock.jsl.api.Property;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PropertyImpl implements Property {

    private final String name;
    private final String value;

    public PropertyImpl(final Property that) {
        this.name = that.getName();
        this.value = that.getValue();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
