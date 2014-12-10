package io.machinecode.chainlink.jsl.xml.execution;

import io.machinecode.chainlink.jsl.core.inherit.execution.InheritableFlow;
import io.machinecode.chainlink.jsl.xml.XmlInheritable;
import io.machinecode.chainlink.jsl.xml.transition.XmlEnd;
import io.machinecode.chainlink.jsl.xml.transition.XmlFail;
import io.machinecode.chainlink.jsl.xml.transition.XmlNext;
import io.machinecode.chainlink.jsl.xml.transition.XmlStop;
import io.machinecode.chainlink.jsl.xml.transition.XmlTransition;
import io.machinecode.chainlink.spi.loader.JobRepository;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import java.util.ArrayList;
import java.util.List;

import static io.machinecode.chainlink.spi.element.Job.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(NONE)
//@XmlType(name = "Flow", propOrder = {
//        "executions",
//        "transitions"
//})
public class XmlFlow extends XmlInheritable<XmlFlow> implements XmlExecution<XmlFlow>, InheritableFlow<XmlFlow, XmlExecution, XmlTransition> {

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
    public XmlFlow inherit(final JobRepository repository, final String defaultJobXml) {
        return FlowTool.inherit(XmlFlow.class, this, repository, defaultJobXml);
    }

    @Override
    public XmlFlow copy() {
        return copy(new XmlFlow());
    }

    @Override
    public XmlFlow copy(final XmlFlow that) {
        return FlowTool.copy(this, that);
    }
}
