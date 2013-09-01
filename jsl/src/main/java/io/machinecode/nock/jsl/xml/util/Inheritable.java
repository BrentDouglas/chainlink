package io.machinecode.nock.jsl.xml.util;

import io.machinecode.nock.jsl.xml.loader.Repository;
import io.machinecode.nock.jsl.xml.execution.XmlExecution;
import io.machinecode.nock.jsl.xml.execution.XmlFlow;
import io.machinecode.nock.jsl.xml.execution.XmlSplit;
import io.machinecode.nock.jsl.xml.execution.XmlStep;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * An element that can inherit from other elements from
 * the JSL Inheritance v1 spec.
 *
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public abstract class Inheritable<T extends Inheritable<T>> implements Copyable<T> {

    @XmlAttribute(name = "abstract", required = false)
    protected Boolean _abstract;

    @XmlAttribute(name = "parent", required = false)
    protected String parent;

    @XmlAttribute(name = "jsl-name", required = false)
    protected String jslName;


    public Boolean isAbstract() {
        return _abstract;
    }

    public T setAbstract(final Boolean _abstract) {
        this._abstract = _abstract;
        return (T)this;
    }

    public String getParent() {
        return parent;
    }

    public T setParent(final String parent) {
        this.parent = parent;
        return (T)this;
    }

    public String getJslName() {
        return jslName;
    }

    public T setJslName(final String jslName) {
        this.jslName = jslName;
        return (T)this;
    }

    @Override
    public T copy(final T that) {
        that.setAbstract(this._abstract);
        that.setParent(this.parent);
        that.setJslName(this.jslName);
        return that;
    }

    /**
     *
     * @param repository
     */
    public abstract T inherit(final Repository repository, final String defaultJobXml);

    /**
     * Pulls attributes from the parent element.
     *
     * @param parent
     */
    protected void inheritingElementRule(final T parent) {
        parent._abstract = null;
        this.parent = null;
        this.jslName = null;
    }

    public static void inheritType(final XmlExecution that, final Repository repository, final String defaultJobXml) {
        if (that instanceof XmlFlow) {
            ((XmlFlow)that).inherit(repository, defaultJobXml);
        }
        if (that instanceof XmlStep) {
            ((XmlStep)that).inherit(repository, defaultJobXml);
        }
        if (that instanceof XmlSplit) {
            for (final XmlExecution type : ((XmlSplit)that).getFlows()) {
                inheritType(type, repository, defaultJobXml);
            }
        }
    }
}
