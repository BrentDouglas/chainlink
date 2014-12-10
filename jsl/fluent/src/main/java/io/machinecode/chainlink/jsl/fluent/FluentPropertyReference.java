package io.machinecode.chainlink.jsl.fluent;

import io.machinecode.chainlink.jsl.core.inherit.InheritablePropertyReference;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class FluentPropertyReference<T extends FluentPropertyReference<T>> implements InheritablePropertyReference<T, FluentProperties> {

    private String ref;
    private FluentProperties properties;

    @Override
    public String getRef() {
        return this.ref;
    }

    @Override
    public T setRef(final String ref) {
        this.ref = ref;
        return (T)this;
    }

    @Override
    public FluentProperties getProperties() {
        return this.properties;
    }

    @Override
    public T setProperties(final FluentProperties properties) {
        this.properties = properties;
        return (T)this;
    }

    public T addProperty(final String name, final String value) {
        if (this.properties == null) {
            this.properties = new FluentProperties();
        }
        this.properties.addProperty(name, value);
        return (T)this;
    }

    @Override
    public T copy(final T that) {
        return PropertyReferenceTool.copy((T)this, that);
    }

    @Override
    public T merge(final T that) {
        return PropertyReferenceTool.merge((T)this, that);
    }
}
