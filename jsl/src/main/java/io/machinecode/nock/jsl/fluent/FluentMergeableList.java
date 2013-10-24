package io.machinecode.nock.jsl.fluent;

import io.machinecode.nock.jsl.inherit.MergeableList;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class FluentMergeableList<T extends FluentMergeableList<T>> implements MergeableList<T> {

    protected boolean merge = true;

    @Override
    public boolean getMerge() {
        return merge;
    }

    @Override
    public T setMerge(final boolean merge) {
        this.merge = merge;
        return (T)this;
    }
}
