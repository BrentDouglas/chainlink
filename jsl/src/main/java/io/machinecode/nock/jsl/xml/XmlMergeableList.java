package io.machinecode.nock.jsl.xml;

import io.machinecode.nock.jsl.inherit.MergeableList;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public abstract class XmlMergeableList<T extends XmlMergeableList<T>> implements MergeableList<T> {

    @XmlAttribute(name = "merge", required = false)
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
