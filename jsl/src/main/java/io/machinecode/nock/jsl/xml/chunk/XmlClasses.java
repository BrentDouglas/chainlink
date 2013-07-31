package io.machinecode.nock.jsl.xml.chunk;

import io.machinecode.nock.jsl.xml.util.MergeableList;
import io.machinecode.nock.jsl.xml.util.Util;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

import static io.machinecode.nock.jsl.xml.XmlJob.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public class XmlClasses extends MergeableList<XmlClasses> {

    @XmlElement(name = "class", namespace = NAMESPACE, required = false)
    private List<String> classes = new ArrayList<String>(0);

    public List<String> getClasses() {
        return classes;
    }

    public XmlClasses setClasses(final List<String> classes) {
        this.classes = classes;
        return this;
    }

    @Override
    public XmlClasses copy() {
        return copy(new XmlClasses());
    }

    @Override
    public XmlClasses copy(final XmlClasses that) {
        that.setClasses(new ArrayList<String>(this.classes));
        return that;
    }

    @Override
    public XmlClasses merge(final XmlClasses that) {
        if (this.merge) {
            this.classes = Util.listRule2(this.classes, that.classes);
        }
        return this;
    }
}
