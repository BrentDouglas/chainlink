package io.machinecode.chainlink.jsl.fluent;

import io.machinecode.chainlink.jsl.core.inherit.MergeableList;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
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
