package io.machinecode.nock.jsl.xml.execution;

import io.machinecode.nock.jsl.inherit.execution.InheritableSplit;
import io.machinecode.nock.spi.JobRepository;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import java.util.ArrayList;
import java.util.List;

import static io.machinecode.nock.spi.element.Job.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
//@XmlType(name = "Split", propOrder = {
//        "flows"
//})
public class XmlSplit implements XmlExecution<XmlSplit>, InheritableSplit<XmlSplit, XmlFlow> {

    @XmlID
    @XmlSchemaType(name = "ID")
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
    public XmlSplit inherit(final JobRepository repository, final String defaultJobXml) {
        return SplitTool.inherit(this, repository, defaultJobXml);
        //final XmlSplit copy = new XmlSplit();
        //copy.setId(this.id);
        //copy.setNext(this.next);
        //copy.setFlows(Util.inheritingList(repository, defaultJobXml, this.flows));
        //return copy;
    }

    @Override
    public XmlSplit copy() {
        return copy(new XmlSplit());
    }

    @Override
    public XmlSplit copy(final XmlSplit that) {
        return SplitTool.copy(this, that);
        //that.setId(this.id);
        //that.setNext(this.next);
        //that.setFlows(Util.copyList(this.flows));
        //return that;
    }
}
