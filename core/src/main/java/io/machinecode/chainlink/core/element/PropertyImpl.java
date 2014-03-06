package io.machinecode.chainlink.core.element;

import io.machinecode.chainlink.spi.element.Property;

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
