package io.machinecode.nock.jsl.xml.execution;

import io.machinecode.nock.jsl.api.execution.Decision;
import io.machinecode.nock.jsl.xml.Repository;
import io.machinecode.nock.jsl.xml.XmlProperties;
import io.machinecode.nock.jsl.xml.transition.XmlEnd;
import io.machinecode.nock.jsl.xml.transition.XmlFail;
import io.machinecode.nock.jsl.xml.transition.XmlNext;
import io.machinecode.nock.jsl.xml.transition.XmlStop;
import io.machinecode.nock.jsl.xml.transition.XmlTransition;
import io.machinecode.nock.jsl.xml.util.Util;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import java.util.ArrayList;
import java.util.List;

import static io.machinecode.nock.jsl.api.Job.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
//@XmlType(name = "Decision", propOrder = {
//        "properties",
//        "transitions"
//})
public class XmlDecision implements XmlExecution<XmlDecision>, Decision {

    @XmlID
    @XmlSchemaType(name = "ID")
    @XmlAttribute(name = "id", required = true)
    private String id;

    @XmlAttribute(name = "ref", required = true)
    private String ref;

    @XmlElement(name = "properties", namespace = NAMESPACE, required = false)
    private XmlProperties properties;

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

    public XmlDecision setId(final String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getRef() {
        return ref;
    }

    public XmlDecision setRef(final String ref) {
        this.ref = ref;
        return this;
    }

    @Override
    public XmlProperties getProperties() {
        return properties;
    }

    public XmlDecision setProperties(final XmlProperties properties) {
        this.properties = properties;
        return this;
    }

    @Override
    public List<XmlTransition> getTransitions() {
        return transitions;
    }

    public XmlDecision setTransitions(final List<XmlTransition> transitions) {
        this.transitions = transitions;
        return this;
    }

    @Override
    public XmlDecision inherit(final Repository repository) {
        return copy();
    }

    @Override
    public XmlDecision copy() {
        return copy(new XmlDecision());
    }

    @Override
    public XmlDecision copy(final XmlDecision that) {
        that.setId(this.id);
        that.setRef(this.ref);
        that.setProperties(Util.copy(this.properties));
        that.setTransitions(Util.copyList(this.transitions));
        return that;
    }
}
