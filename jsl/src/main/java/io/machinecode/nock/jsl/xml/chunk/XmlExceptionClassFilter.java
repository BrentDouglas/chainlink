package io.machinecode.nock.jsl.xml.chunk;

import io.machinecode.nock.jsl.api.chunk.ExceptionClassFilter;
import io.machinecode.nock.jsl.xml.util.MergeableList;
import io.machinecode.nock.jsl.xml.util.Util;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

import static io.machinecode.nock.jsl.xml.XmlJob.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public class XmlExceptionClassFilter extends MergeableList<XmlExceptionClassFilter> implements ExceptionClassFilter {

    @XmlElement(name = "include", namespace = NAMESPACE, required = false)
    private List<XmlExceptionClass> includes;

    @XmlElement(name = "exclude", namespace = NAMESPACE, required = false)
    private List<XmlExceptionClass> excludes;


    public List<XmlExceptionClass> getIncludes() {
        return includes;
    }

    public XmlExceptionClassFilter setIncludes(final List<XmlExceptionClass> includes) {
        this.includes = includes;
        return this;
    }

    public List<XmlExceptionClass> getExcludes() {
        return excludes;
    }

    public XmlExceptionClassFilter setExcludes(final List<XmlExceptionClass> excludes) {
        this.excludes = excludes;
        return this;
    }

    @Override
    public XmlExceptionClassFilter copy() {
        return copy(new XmlExceptionClassFilter());
    }

    @Override
    public XmlExceptionClassFilter copy(final XmlExceptionClassFilter that) {
        that.setIncludes(Util.copyList(this.includes));
        that.setExcludes(Util.copyList(this.excludes));
        return that;
    }

    @Override
    public XmlExceptionClassFilter merge(final XmlExceptionClassFilter that) {
        if (this.merge) {
            this.includes = Util.listRule(this.includes, that.includes);
            this.excludes = Util.listRule(this.excludes, that.includes);
        }
        return this;
    }
}
