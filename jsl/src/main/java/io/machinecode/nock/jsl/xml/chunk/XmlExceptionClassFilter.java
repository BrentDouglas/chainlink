package io.machinecode.nock.jsl.xml.chunk;

import io.machinecode.nock.jsl.xml.util.Mergeable;
import io.machinecode.nock.jsl.xml.util.Util;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import static io.machinecode.nock.jsl.xml.XmlJob.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public class XmlExceptionClassFilter implements Mergeable<XmlExceptionClassFilter> {

    @XmlElement(name = "include", namespace = NAMESPACE, required = false)
    private XmlClasses includes;

    @XmlElement(name = "exclude", namespace = NAMESPACE, required = false)
    private XmlClasses excludes;


    public XmlClasses getIncludes() {
        return includes;
    }

    public XmlExceptionClassFilter setIncludes(final XmlClasses includes) {
        this.includes = includes;
        return this;
    }

    public XmlClasses getExcludes() {
        return excludes;
    }

    public XmlExceptionClassFilter setExcludes(final XmlClasses excludes) {
        this.excludes = excludes;
        return this;
    }

    @Override
    public XmlExceptionClassFilter copy() {
        return copy(new XmlExceptionClassFilter());
    }

    @Override
    public XmlExceptionClassFilter copy(final XmlExceptionClassFilter that) {
        that.setIncludes(Util.copy(this.includes));
        that.setExcludes(Util.copy(this.excludes));
        return that;
    }

    @Override
    public XmlExceptionClassFilter merge(final XmlExceptionClassFilter that) {
        this.setIncludes(Util.merge(this.includes, that.includes));
        this.setExcludes(Util.merge(this.excludes, that.includes));
        return this;
    }
}
