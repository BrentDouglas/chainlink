package io.machinecode.chainlink.core.jsl.xml.execution;

import io.machinecode.chainlink.spi.jsl.inherit.execution.InheritableStep;
import io.machinecode.chainlink.core.jsl.xml.XmlInheritable;
import io.machinecode.chainlink.core.jsl.xml.XmlListeners;
import io.machinecode.chainlink.core.jsl.xml.XmlProperties;
import io.machinecode.chainlink.core.jsl.xml.partition.XmlPartition;
import io.machinecode.chainlink.core.jsl.xml.task.XmlBatchlet;
import io.machinecode.chainlink.core.jsl.xml.task.XmlChunk;
import io.machinecode.chainlink.core.jsl.xml.task.XmlTask;
import io.machinecode.chainlink.core.jsl.xml.transition.XmlEnd;
import io.machinecode.chainlink.core.jsl.xml.transition.XmlFail;
import io.machinecode.chainlink.core.jsl.xml.transition.XmlNext;
import io.machinecode.chainlink.core.jsl.xml.transition.XmlStop;
import io.machinecode.chainlink.core.jsl.xml.transition.XmlTransition;
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
//@XmlType(name = "Step", propOrder = {
//        "properties",
//        "listeners",
//        "task",
//        "partition",
//        "transitions"
//})
public class XmlStep
        extends XmlInheritable<XmlStep>
        implements XmlExecution<XmlStep>, InheritableStep<XmlStep, XmlProperties, XmlListeners, XmlTask, XmlTransition, XmlPartition> {

    @XmlID
    @XmlSchemaType(name = "ID")
    @XmlAttribute(name = "id", required = true)
    private String id;

    @XmlAttribute(name = "next", required = false)
    private String next;

    @XmlAttribute(name = "start-limit", required = false)
    private String startLimit = ZERO;

    @XmlAttribute(name = "allow-start-if-complete", required = false)
    private String allowStartIfComplete = "false";

    @XmlElement(name = "properties", namespace = NAMESPACE, required = false)
    private XmlProperties properties;

    @XmlElement(name = "listeners", namespace = NAMESPACE, required = false)
    private XmlListeners listeners;

    @XmlElements({
            @XmlElement(name = "batchlet", namespace = NAMESPACE, required = false, type = XmlBatchlet.class),
            @XmlElement(name = "chunk", namespace = NAMESPACE, required = false, type = XmlChunk.class)
    })
    private XmlTask task;

    @XmlElement(name = "partition", namespace = NAMESPACE, required = false, type = XmlPartition.class)
    private XmlPartition partition;

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

    public XmlStep setId(final String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getNext() {
        return next;
    }

    public XmlStep setNext(final String next) {
        this.next = next;
        return this;
    }

    @Override
    public String getStartLimit() {
        return startLimit;
    }

    public XmlStep setStartLimit(final String startLimit) {
        this.startLimit = startLimit;
        return this;
    }

    @Override
    public String getAllowStartIfComplete() {
        return allowStartIfComplete;
    }

    public XmlStep setAllowStartIfComplete(final String allowStartIfComplete) {
        this.allowStartIfComplete = allowStartIfComplete;
        return this;
    }

    @Override
    public XmlProperties getProperties() {
        return properties;
    }

    public XmlStep setProperties(final XmlProperties properties) {
        this.properties = properties;
        return this;
    }

    @Override
    public XmlListeners getListeners() {
        return listeners;
    }

    public XmlStep setListeners(final XmlListeners listeners) {
        this.listeners = listeners;
        return this;
    }

    @Override
    public XmlTask getTask() {
        return task;
    }

    public XmlStep setTask(final XmlTask task) {
        this.task = task;
        return this;
    }

    @Override
    public List<XmlTransition> getTransitions() {
        return transitions;
    }

    public XmlStep setTransitions(final List<XmlTransition> transitions) {
        this.transitions = transitions;
        return this;
    }

    @Override
    public XmlPartition getPartition() {
        return partition;
    }

    public XmlStep setPartition(final XmlPartition partition) {
        this.partition = partition;
        return this;
    }

    @Override
    public XmlStep inherit(final JobRepository repository, final String defaultJobXml) {
        return StepTool.inherit(XmlStep.class, this, repository, defaultJobXml);
    }

    @Override
    public XmlStep copy() {
        return copy(new XmlStep());
    }

    @Override
    public XmlStep copy(final XmlStep that) {
        return StepTool.copy(this, that);
    }
}
