package io.machinecode.nock.jsl.xml.type;

import io.machinecode.nock.jsl.xml.transition.XmlEnd;
import io.machinecode.nock.jsl.xml.transition.XmlFail;
import io.machinecode.nock.jsl.xml.transition.XmlNext;
import io.machinecode.nock.jsl.xml.transition.XmlStop;
import io.machinecode.nock.jsl.xml.transition.XmlTransition;
import io.machinecode.nock.jsl.xml.util.Inheritable;
import io.machinecode.nock.jsl.xml.Repository;
import io.machinecode.nock.jsl.xml.util.Util;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import java.util.ArrayList;
import java.util.List;

import static io.machinecode.nock.jsl.xml.XmlJob.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@javax.xml.bind.annotation.XmlType(name = "flow")
@XmlAccessorType(NONE)
public class XmlFlow extends Inheritable<XmlFlow> implements XmlType<XmlFlow> {

    @XmlID
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
    private List<XmlType> types = new ArrayList<XmlType>();

    @XmlElements({
            @XmlElement(name = "end", namespace = NAMESPACE, required = false, type = XmlEnd.class),
            @XmlElement(name = "fail", namespace = NAMESPACE, required = false, type = XmlFail.class),
            @XmlElement(name = "next", namespace = NAMESPACE, required = false, type = XmlNext.class),
            @XmlElement(name = "stop", namespace = NAMESPACE, required = false, type = XmlStop.class)
    })
    private List<XmlTransition> transitions = new ArrayList<XmlTransition>();


    public String getId() {
        return id;
    }

    public XmlFlow setId(final String id) {
        this.id = id;
        return this;
    }

    public String getNext() {
        return next;
    }

    public XmlFlow setNext(final String next) {
        this.next = next;
        return this;
    }

    public List<XmlType> getTypes() {
        return types;
    }

    public XmlFlow setTypes(final List<XmlType> types) {
        this.types = types;
        return this;
    }

    public List<XmlTransition> getTransitions() {
        return transitions;
    }

    public XmlFlow setTransitions(final List<XmlTransition> transitions) {
        this.transitions = transitions;
        return this;
    }

    @Override
    public XmlFlow inherit(final Repository repository) {
        final XmlFlow copy = this.copy();
        if (this.parent != null) {
            final XmlFlow that = repository.findParent(XmlFlow.class, copy);

            that.transitions.clear(); // 4.6.3.1 Drop parent transitions

            copy.types.clear(); // 4.6.3.2

            copy.inheritingElementRule(that); // 4.6.3.3

            // 4.6.3.4
            copy.id = Util.attributeRule(copy.id, that.id); // 4.1
            copy.next = Util.attributeRule(copy.next, that.next); // 4.1

            copy.types = Util.inheritingList(repository, that.types);
        } else {
            copy.types = Util.inheritingList(repository, this.types);
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
        that.setTypes(Util.copyList(this.types)); //TODO Skip these on inherit call
        that.setTransitions(Util.copyList(this.transitions));
        return that;
    }
}
