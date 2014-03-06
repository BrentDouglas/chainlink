package io.machinecode.chainlink.jsl.xml;

import io.machinecode.chainlink.jsl.inherit.InheritableJob;
import io.machinecode.chainlink.jsl.xml.execution.XmlDecision;
import io.machinecode.chainlink.jsl.xml.execution.XmlExecution;
import io.machinecode.chainlink.jsl.xml.execution.XmlFlow;
import io.machinecode.chainlink.jsl.xml.execution.XmlSplit;
import io.machinecode.chainlink.jsl.xml.execution.XmlStep;
import io.machinecode.chainlink.spi.loader.JobRepository;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import java.util.ArrayList;
import java.util.List;

import static io.machinecode.chainlink.spi.element.Job.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlRootElement(namespace = NAMESPACE, name = "job")
@XmlAccessorType(NONE)
//@XmlType(name = "Job", propOrder = {
//        "properties",
//        "listeners",
//        "executions"
//})
public class XmlJob extends XmlInheritable<XmlJob> implements InheritableJob<XmlJob, XmlProperties, XmlListeners, XmlExecution> {

    @XmlID
    @XmlSchemaType(name = "ID")
    @XmlAttribute(name = "id", required = true)
    private String id;

    @XmlAttribute(name = "version", required = false)
    private static final String version = "1.0"; //fixed

    @XmlAttribute(name = "restartable", required = false)
    private String restartable = "true";

    @XmlElement(name = "properties", namespace = NAMESPACE, required = false, type = XmlProperties.class)
    private XmlProperties properties;

    @XmlElement(name = "listeners", namespace = NAMESPACE, required = false, type = XmlListeners.class)
    private XmlListeners listeners;

    @XmlElements({
            @XmlElement(name = "decision", required = false, namespace = NAMESPACE, type = XmlDecision.class),
            @XmlElement(name = "flow", required = false, namespace = NAMESPACE, type = XmlFlow.class),
            @XmlElement(name = "split", required = false, namespace = NAMESPACE, type = XmlSplit.class),
            @XmlElement(name = "step", required = false, namespace = NAMESPACE, type = XmlStep.class)
    })
    private List<XmlExecution> executions = new ArrayList<XmlExecution>(0);


    @Override
    public String getId() {
        return id;
    }

    public XmlJob setId(final String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getRestartable() {
        return restartable;
    }

    public XmlJob setRestartable(final String restartable) {
        this.restartable = restartable;
        return this;
    }

    @Override
    public XmlProperties getProperties() {
        return properties;
    }

    public XmlJob setProperties(final XmlProperties properties) {
        this.properties = properties;
        return this;
    }

    @Override
    public XmlListeners getListeners() {
        return listeners;
    }

    public XmlJob setListeners(final XmlListeners listeners) {
        this.listeners = listeners;
        return this;
    }

    @Override
    public List<XmlExecution> getExecutions() {
        return executions;
    }

    public XmlJob setExecutions(final List<XmlExecution> executions) {
        this.executions = executions;
        return this;
    }

    @Override
    public XmlJob inherit(final JobRepository repository, final String defaultJobXml) {
        return JobTool.inherit(XmlJob.class, this, repository, defaultJobXml);
        //final XmlJob copy = this.copy();
        //if (copy.parent != null) {
        //    final XmlJob that = repository.findParent(XmlJob.class, copy, defaultJobXml);
//
        //    that.getExecutions().clear(); // 4.6.1.1
//
        //    copy.inheritingElementRule(that); // 4.6.1.2
//
        //    // 4.4
        //    copy.properties = Util.merge(copy.properties, that.properties);
        //    copy.listeners = Util.merge(copy.listeners, that.listeners);
        //    // 4.1
        //    copy.restartable = Util.attributeRule(copy.restartable, that.restartable); // 4.4.1
        //}
        //copy.executions = Util.inheritingList(repository, defaultJobXml, this.executions);
        //return copy;
    }

    @Override
    public XmlJob copy() {
        return copy(new XmlJob());
    }

    @Override
    public XmlJob copy(final XmlJob that) {
        return JobTool.copy(this, that);
    }
}
