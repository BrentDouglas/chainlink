package io.machinecode.nock.jsl.xml.execution;

import io.machinecode.nock.spi.element.execution.Flow;
import io.machinecode.nock.jsl.xml.loader.Repository;
import io.machinecode.nock.jsl.xml.transition.XmlEnd;
import io.machinecode.nock.jsl.xml.transition.XmlFail;
import io.machinecode.nock.jsl.xml.transition.XmlNext;
import io.machinecode.nock.jsl.xml.transition.XmlStop;
import io.machinecode.nock.jsl.xml.transition.XmlTransition;
import io.machinecode.nock.jsl.xml.util.Inheritable;
import io.machinecode.nock.jsl.xml.util.Util;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
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
//@XmlType(name = "Flow", propOrder = {
//        "executions",
//        "transitions"
//})
public class XmlFlow extends Inheritable<XmlFlow> implements XmlExecution<XmlFlow>, Flow {

    @XmlID
    @XmlSchemaType(name = "ID")
    @XmlAttribute(name = "id", required = true)
    private String id;

    @XmlAttribute(name = "next", required = true)
    private String next;

    @XmlElements({
            @XmlElement(name = "decision", namespace = NAMESPACE, required = false, type = XmlDecision.class),
            @XmlElement(name = "flow", namespace = NAMESPACE, required = false, type = XmlFlow.class),
            @XmlElement(name = "split", namespace = NAMESPACE, required = false, type = XmlSplit.class),
            @XmlElement(name = "step", namespace = NAMESPACE, required = false, type = XmlStep.class)
    })
    private List<XmlExecution> executions = new ArrayList<XmlExecution>();

    @XmlElements({
            @XmlElement(name = "end", namespace = NAMESPACE, required = false, type = XmlEnd.class),
            @XmlElement(name = "fail", namespace = NAMESPACE, required = false, type = XmlFail.class),
            @XmlElement(name = "next", namespace = NAMESPACE, required = false, type = XmlNext.class),
            @XmlElement(name = "stop", namespace = NAMESPACE, required = false, type = XmlStop.class)
    })
    private List<XmlTransition> transitions = new ArrayList<XmlTransition>();


    @Override
    public String getId() {
        return id;
    }

    public XmlFlow setId(final String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getNext() {
        return next;
    }

    public XmlFlow setNext(final String next) {
        this.next = next;
        return this;
    }

    @Override
    public List<XmlExecution> getExecutions() {
        return executions;
    }

    public XmlFlow setExecutions(final List<XmlExecution> executions) {
        this.executions = executions;
        return this;
    }

    @Override
    public List<XmlTransition> getTransitions() {
        return transitions;
    }

    public XmlFlow setTransitions(final List<XmlTransition> transitions) {
        this.transitions = transitions;
        return this;
    }

    @Override
    public XmlFlow inherit(final Repository repository, final String defaultJobXml) {
        final XmlFlow copy = this.copy();
        if (this.parent != null) {
            final XmlFlow that = repository.findParent(XmlFlow.class, copy, defaultJobXml);

            that.transitions.clear(); // 4.6.3.1 Drop parent transitions

            copy.executions.clear(); // 4.6.3.2

            copy.inheritingElementRule(that); // 4.6.3.3

            // 4.6.3.4
            copy.id = Util.attributeRule(copy.id, that.id); // 4.1
            copy.next = Util.attributeRule(copy.next, that.next); // 4.1

            copy.executions = Util.inheritingList(repository, defaultJobXml, that.executions);
        } else {
            copy.executions = Util.inheritingList(repository, defaultJobXml, this.executions);
        }

        return copy;
    }

    @Override
    public XmlFlow copy() {
        return copy(new XmlFlow());
    }

    @Override
    public XmlFlow copy(final XmlFlow that) {
        super.copy(that);
        that.setId(this.id);
        that.setNext(this.next);
        that.setExecutions(Util.copyList(this.executions)); //TODO Skip these on inherit call
        that.setTransitions(Util.copyList(this.transitions));
        return that;
    }
}
