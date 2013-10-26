package io.machinecode.nock.jsl.fluent;

import io.machinecode.nock.jsl.inherit.InheritableBase;

/**
 * An elementName that can inherit from other elements from
 * the JSL Inheritance v1 spec.
 *
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class FluentInheritableBase<T extends FluentInheritableBase<T>> implements InheritableBase<T> {

    protected Boolean _abstract;
    protected String parent;
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
    }

    protected void inheritingElementRule(final T parent) {
        BaseTool.inheritingElementRule(this, parent);
    }
}
