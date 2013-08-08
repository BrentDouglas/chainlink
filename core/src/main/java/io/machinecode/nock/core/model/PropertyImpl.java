package io.machinecode.nock.core.model;

import io.machinecode.nock.jsl.api.Property;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PropertyImpl implements Property {

    private final String name;
    private final String value;

    public PropertyImpl(final String name, final String value) {
        this.name = name;
        this.value = value;
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
