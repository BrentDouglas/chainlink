package io.machinecode.chainlink.jsl.xml;

import io.machinecode.chainlink.jsl.core.inherit.MergeableList;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
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
