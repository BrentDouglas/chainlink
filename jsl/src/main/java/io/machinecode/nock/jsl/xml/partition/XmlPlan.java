package io.machinecode.nock.jsl.xml.partition;

import io.machinecode.nock.spi.element.partition.Plan;
import io.machinecode.nock.jsl.xml.XmlProperties;
import io.machinecode.nock.jsl.xml.util.Util;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import static io.machinecode.nock.spi.element.Job.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
//@XmlType(name = "PartitionPlan", propOrder = {
//        "properties"
//})
public class XmlPlan implements XmlStrategy<XmlPlan>, Plan {

    @XmlAttribute(name = "partitions", required = false)
    private String partitions = ONE;

    @XmlAttribute(name = "threads", required = false)
    private String threads;

    @XmlElement(name = "properties", namespace = NAMESPACE, required = false)
    private XmlProperties properties;

    @Override
    public String getPartitions() {
        return partitions;
    }

    public XmlPlan setPartitions(final String partitions) {
        this.partitions = partitions;
        return this;
    }

    @Override
    public String getThreads() {
        return threads;
    }

    public XmlPlan setThreads(final String threads) {
        this.threads = threads;
        return this;
    }

    @Override
    public XmlProperties getProperties() {
        return properties;
    }

    public XmlPlan setProperties(final XmlProperties properties) {
        this.properties = properties;
        return this;
    }

    @Override
    public XmlPlan copy() {
        return copy(new XmlPlan());
    }

    @Override
    public XmlPlan copy(final XmlPlan that) {
        that.setPartitions(this.partitions);
        that.setThreads(this.threads);
        that.setProperties(Util.copy(this.properties));
        return that;
    }
}
