package io.machinecode.nock.jsl.impl.type;

import io.machinecode.nock.jsl.api.type.Type;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class TypeImpl implements Type {

    private final String id;

    public TypeImpl(final Type that) {
        this.id = that.getId();
    }

    @Override
    public String getId() {
        return this.id;
    }
}
