package io.machinecode.chainlink.jsl.xml;

import io.machinecode.chainlink.jsl.inherit.InheritableBase;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * An elementName that can inherit from other elements from
 * the JSL Inheritance v1 spec.
 *
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public abstract class XmlInheritable<T extends XmlInheritable<T>> implements InheritableBase<T> {

    @XmlAttribute(name = "abstract", required = false)
    protected Boolean _abstract;

    @XmlAttribute(name = "parent", required = false)
    protected String parent;

    @XmlAttribute(name = "jsl-name", required = false)
    protected String jslName;


    @Override
    public Boolean isAbstract() {
        return _abstract;
    }

    @Override
    public T setAbstract(final Boolean _abstract) {
        this._abstract = _abstract;
        return (T)this;
    }

    @Override
    public String getParent() {
        return parent;
    }

    @Override
    public T setParent(final String parent) {
        this.parent = parent;
        return (T)this;
    }

    @Override
    public String getJslName() {
        return jslName;
    }

    @Override
    public T setJslName(final String jslName) {
        this.jslName = jslName;
        return (T)this;
    }

    @Override
    public T copy(final T that) {
        return BaseTool.copy((T)this, that);
        //that.setAbstract(this._abstract);
        //that.setParent(this.parent);
        //that.setJslName(this.jslName);
        //return that;
    }

    protected void inheritingElementRule(final T parent) {
        BaseTool.inheritingElementRule(this, parent);
    }
}
