package io.machinecode.nock.jsl.fluent;

import io.machinecode.nock.jsl.inherit.InheritableProperty;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentProperty implements InheritableProperty<FluentProperty> {

    private String name;
    private String value;

    @Override
    public String getName() {
            return this.name;
    }

    public FluentProperty setName(final String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public FluentProperty setValue(final String value) {
        this.value = value;
        return this;
    }

    @Override
    public FluentProperty copy() {
        return copy(new FluentProperty());
    }

    @Override
    public FluentProperty copy(final FluentProperty that) {
        return PropertyTool.copy(this, that);
    }
}
