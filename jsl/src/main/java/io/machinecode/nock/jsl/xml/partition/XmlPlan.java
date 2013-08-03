package io.machinecode.nock.jsl.xml.partition;

import io.machinecode.nock.jsl.api.partition.Plan;
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
public class XmlPlan implements XmlStrategy<XmlPlan>, Plan {

    @XmlAttribute(name = "partitions", required = false)
    private int partitions = 1;

    @XmlAttribute(name = "threads", required = false)
    private Integer threads;

    @XmlElement(name = "properties", namespace = NAMESPACE, required = false)
    private XmlProperties properties;

    @Override
    public int getPartitions() {
        return partitions;
    }

    public XmlPlan setPartitions(final int partitions) {
        this.partitions = partitions;
        return this;
    }

    @Override
    public Integer getThreads() {
        return threads;
    }

    public XmlPlan setThreads(final Integer threads) {
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
