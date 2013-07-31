package io.machinecode.nock.jsl.fluent;

import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.PropertyReference;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class FluentPropertyReference<T extends FluentPropertyReference<T>> implements PropertyReference {

    private String ref;
    private FluentProperties properties = new FluentProperties();

    @Override
    public String getRef() {
        return this.ref;
    }

    public T setRef(final String ref) {
        this.ref = ref;
        return (T)this;
    }

    @Override
    public Properties getProperties() {
        return this.properties;
    }

    public T addProperty(final String name, final String value) {
        this.properties.addProperty(name, value);
        return (T)this;
    }
}
