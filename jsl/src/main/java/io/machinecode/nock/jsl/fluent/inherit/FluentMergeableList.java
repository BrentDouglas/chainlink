package io.machinecode.nock.jsl.fluent.inherit;

import io.machinecode.nock.spi.Mergeable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class FluentMergeableList<T extends FluentMergeableList<T>> implements Mergeable<T> {

    protected boolean merge = true;

    public boolean getMerge() {
        return merge;
    }

    public T setMerge(final boolean merge) {
        this.merge = merge;
        return (T)this;
    }

    /**
     * {@inheritDoc}
     */
    public abstract T copy();

    /**
     * {@inheritDoc}
     */
    public abstract T copy(final T that);

    /**
     * {@inheritDoc}
     */
    public abstract T merge(final T that);
}
