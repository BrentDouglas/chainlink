package io.machinecode.nock.jsl.xml.partition;

import io.machinecode.nock.jsl.api.partition.PartitionPlan;
import io.machinecode.nock.jsl.xml.XmlProperties;
import io.machinecode.nock.jsl.xml.util.Util;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import static io.machinecode.nock.jsl.xml.XmlJob.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public class XmlPartitionPlan implements XmlMapper<XmlPartitionPlan>, PartitionPlan {

    @XmlAttribute(name = "partitions", required = false)
    private String partitions;

    @XmlAttribute(name = "threads", required = false)
    private String threads;

    @XmlElement(name = "properties", namespace = NAMESPACE, required = false)
    private XmlProperties properties;

    @Override
    public String getPartitions() {
        return partitions;
    }

    public XmlPartitionPlan setPartitions(final String partitions) {
        this.partitions = partitions;
        return this;
    }

    @Override
    public String getThreads() {
        return threads;
    }

    public XmlPartitionPlan setThreads(final String threads) {
        this.threads = threads;
        return this;
    }

    @Override
    public XmlProperties getProperties() {
        return properties;
    }

    public XmlPartitionPlan setProperties(final XmlProperties properties) {
        this.properties = properties;
        return this;
    }

    @Override
    public XmlPartitionPlan copy() {
        return copy(new XmlPartitionPlan());
    }

    @Override
    public XmlPartitionPlan copy(final XmlPartitionPlan that) {
        that.setPartitions(this.partitions);
        that.setThreads(this.threads);
        that.setProperties(Util.copy(this.properties));
        return that;
    }
}
