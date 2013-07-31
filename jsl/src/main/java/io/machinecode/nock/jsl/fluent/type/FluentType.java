package io.machinecode.nock.jsl.fluent.type;

import io.machinecode.nock.jsl.api.type.Type;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class FluentType<T extends FluentType<T>> implements Type {

    private String id;

    @Override
    public String getId() {
        return this.id;
    }

    public T setId(final String id) {
        this.id = id;
        return (T)this;
    }
}
