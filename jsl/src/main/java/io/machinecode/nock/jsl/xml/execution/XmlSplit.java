package io.machinecode.nock.jsl.xml.execution;

import io.machinecode.nock.jsl.api.execution.Split;
import io.machinecode.nock.jsl.xml.Repository;
import io.machinecode.nock.jsl.xml.util.Util;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

import static io.machinecode.nock.jsl.xml.XmlJob.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlType(name = "split")
@XmlAccessorType(NONE)
public class XmlSplit implements XmlExecution<XmlSplit>, Split {

    @XmlID
    @XmlAttribute(name = "id", required = true)
    private String id;

    @XmlAttribute(name = "next", required = true)
    private String next;

    @XmlElement(name = "flow", namespace = NAMESPACE, required = false)
    private List<XmlFlow> flows = new ArrayList<XmlFlow>();


    @Override
    public String getId() {
        return id;
    }

    public XmlSplit setId(final String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getNext() {
        return next;
    }

    public XmlSplit setNext(final String next) {
        this.next = next;
        return this;
    }

    @Override
    public List<XmlFlow> getFlows() {
        return flows;
    }

    public XmlSplit setFlows(final List<XmlFlow> flows) {
        this.flows = flows;
        return this;
    }

    @Override
    public XmlSplit inherit(final Repository repository) {
        final XmlSplit copy = new XmlSplit();
        copy.setId(this.id);
        copy.setNext(this.next);
        copy.setFlows(Util.inheritingList(repository, this.flows));
        return copy;
    }

    @Override
    public XmlSplit copy() {
        return copy(new XmlSplit());
    }

    @Override
    public XmlSplit copy(final XmlSplit that) {
        that.setId(this.id);
        that.setNext(this.next);
        that.setFlows(Util.copyList(this.flows));
        return that;
    }
}
