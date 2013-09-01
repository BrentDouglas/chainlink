package io.machinecode.nock.jsl.fluent;

import io.machinecode.nock.spi.element.Property;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentProperty<T extends FluentProperty<T>> implements Property {

    private String name;
    private String value;

    @Override
    public String getName() {
            return this.name;
    }

    public T setName(final String name) {
        this.name = name;
        return (T)this;
    }

    @Override
    public String getValue() {
            return this.value;
    }

    public T setValue(final String value) {
        this.value = value;
        return (T)this;
    }
}
