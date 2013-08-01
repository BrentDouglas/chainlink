package io.machinecode.nock.jsl.xml.chunk;

import io.machinecode.nock.jsl.api.chunk.ExceptionClass;
import io.machinecode.nock.jsl.xml.util.Mergeable;
import io.machinecode.nock.jsl.xml.util.Util;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import static io.machinecode.nock.jsl.xml.XmlJob.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public class XmlExceptionClass implements Mergeable<XmlExceptionClass>, ExceptionClass {

    @XmlAttribute(name = "class", namespace = NAMESPACE, required = true)
    private String className;

    public String getClassName() {
        return className;
    }

    public XmlExceptionClass setClassName(final String className) {
        this.className = className;
        return this;
    }

    @Override
    public XmlExceptionClass copy() {
        return copy(new XmlExceptionClass());
    }

    @Override
    public XmlExceptionClass copy(final XmlExceptionClass that) {
        that.setClassName(this.className);
        return that;
    }

    @Override
    public XmlExceptionClass merge(final XmlExceptionClass that) {
        this.setClassName(Util.attributeRule(this.className, that.className));
        return this;
    }
}
