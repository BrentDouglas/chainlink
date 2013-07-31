package io.machinecode.nock.jsl.xml.util;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public abstract class MergeableList<T extends MergeableList<T>> implements Mergeable<T> {

    @XmlAttribute(name = "merge", required = false)
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
