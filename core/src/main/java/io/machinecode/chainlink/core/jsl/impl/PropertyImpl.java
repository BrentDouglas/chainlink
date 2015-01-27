package io.machinecode.chainlink.core.jsl.impl;

import io.machinecode.chainlink.spi.jsl.Property;
import io.machinecode.chainlink.spi.util.Pair;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class PropertyImpl implements Property, Serializable, Pair<String,String> {
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
