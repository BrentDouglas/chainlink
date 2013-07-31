package io.machinecode.nock.jsl.xml.type;

import io.machinecode.nock.jsl.xml.XmlBatchlet;
import io.machinecode.nock.jsl.xml.XmlListeners;
import io.machinecode.nock.jsl.xml.XmlPart;
import io.machinecode.nock.jsl.xml.XmlProperties;
import io.machinecode.nock.jsl.xml.chunk.XmlChunk;
import io.machinecode.nock.jsl.xml.partition.XmlPartition;
import io.machinecode.nock.jsl.xml.transition.XmlEnd;
import io.machinecode.nock.jsl.xml.transition.XmlFail;
import io.machinecode.nock.jsl.xml.transition.XmlNext;
import io.machinecode.nock.jsl.xml.transition.XmlStop;
import io.machinecode.nock.jsl.xml.transition.XmlTransition;
import io.machinecode.nock.jsl.xml.util.Inheritable;
import io.machinecode.nock.jsl.xml.Repository;
import io.machinecode.nock.jsl.xml.util.Util;

import javax.batch.operations.JobStartException;
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
@javax.xml.bind.annotation.XmlType(name = "step")
@XmlAccessorType(NONE)
public class XmlStep extends Inheritable<XmlStep> implements XmlType<XmlStep> {

    @XmlID
    @XmlAttribute(name = "id", required = true)
    private String id;

    @XmlAttribute(name = "next", required = false)
    private String next;

    @XmlAttribute(name = "start-limit", required = false)
    private String startLimit;

    @XmlAttribute(name = "allow-start-if-complete", required = false)
    private String allowStartIfComplete;

    @XmlElement(name = "listeners", namespace = NAMESPACE, required = false)
    private XmlListeners listeners;

    @XmlElement(name = "properties", namespace = NAMESPACE, required = false)
    private XmlProperties properties;

    @XmlElements({
            @XmlElement(name = "batchlet", namespace = NAMESPACE, required = false, type = XmlBatchlet.class),
            @XmlElement(name = "chunk", namespace = NAMESPACE, required = false, type = XmlChunk.class)
    })
    private XmlPart part;

    @XmlElement(name = "partition", namespace = NAMESPACE, required = false, type = XmlPartition.class)
    private XmlPartition partition;

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

    public XmlStep setId(final String id) {
        this.id = id;
        return this;
    }

    public String getNext() {
        return next;
    }

    public XmlStep setNext(final String next) {
        this.next = next;
        return this;
    }

    public String getStartLimit() {
        return startLimit;
    }

    public XmlStep setStartLimit(final String startLimit) {
        this.startLimit = startLimit;
        return this;
    }

    public String getAllowStartIfComplete() {
        return allowStartIfComplete;
    }

    public XmlStep setAllowStartIfComplete(final String allowStartIfComplete) {
        this.allowStartIfComplete = allowStartIfComplete;
        return this;
    }

    public XmlListeners getListeners() {
        return listeners;
    }

    public XmlStep setListeners(final XmlListeners listeners) {
        this.listeners = listeners;
        return this;
    }

    public XmlProperties getProperties() {
        return properties;
    }

    public XmlStep setProperties(final XmlProperties properties) {
        this.properties = properties;
        return this;
    }

    public XmlPart getPart() {
        return part;
    }

    public XmlBatchlet getBatchlet() {
        return part instanceof XmlBatchlet
                ? (XmlBatchlet)part
                : null;
    }

    public XmlChunk getChunk() {
        return part instanceof XmlChunk
                ? (XmlChunk) part
                : null;
    }

    public XmlStep setPart(final XmlPart part) {
        this.part = part;
        return this;
    }

    public List<XmlTransition> getTransitions() {
        return transitions;
    }

    public XmlStep setTransitions(final List<XmlTransition> transitions) {
        this.transitions = transitions;
        return this;
    }

    public XmlPartition getPartition() {
        return partition;
    }

    public XmlStep setPartition(final XmlPartition partition) {
        this.partition = partition;
        return this;
    }

    @Override
    public XmlStep inherit(final Repository repository) {
        final XmlStep copy = this.copy();
        if (copy.parent != null) {
            final XmlStep that = repository.findParent(XmlStep.class, copy);

            copy.inheritingElementRule(that); // 4.6.2.1

            copy.partition = Util.attributeRule(copy.partition, that.partition); // 4.6.2.2

            copy.next = Util.attributeRule(copy.next, that.next); // 4.1
            copy.startLimit = Util.attributeRule(copy.startLimit, that.startLimit); // 4.1
            copy.allowStartIfComplete = Util.attributeRule(copy.allowStartIfComplete, that.allowStartIfComplete); // 4.1

            // Skip types
            // 4.3
            copy.properties = Util.merge(copy.properties, that.properties);
            copy.listeners = Util.merge(copy.listeners, that.listeners);
            copy.transitions = Util.listRule(copy.transitions, that.transitions);
            // 4.1
            if (copy.part instanceof XmlChunk && that.part instanceof XmlBatchlet
                    || copy.part instanceof XmlBatchlet && that.part instanceof XmlChunk) {
                throw new JobStartException();
            }
            copy.part = Util.recursiveElementRule(copy.part, that.part, repository); // 4.4.1
        }
        return copy;
    }

    @Override
    public XmlStep copy() {
        return copy(new XmlStep());
    }

    @Override
    public XmlStep copy(final XmlStep that) {
        super.copy(that);
        that.setId(this.id);
        that.setNext(this.next);
        that.setStartLimit(this.startLimit);
        that.setAllowStartIfComplete(this.allowStartIfComplete);
        that.setProperties(Util.copy(this.properties));
        that.setListeners(Util.copy(this.listeners));
        that.setPart(Util.copy(this.part));
        that.setTransitions(Util.copyList(this.transitions));
        return that;
    }
}
