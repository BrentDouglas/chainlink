package io.machinecode.chainlink.core.element;

import io.machinecode.chainlink.spi.element.Property;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class PropertyImpl implements Property, Serializable {
    private static final long serialVersionUID = 1L;

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
