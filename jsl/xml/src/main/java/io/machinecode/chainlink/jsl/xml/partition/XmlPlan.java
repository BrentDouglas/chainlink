package io.machinecode.chainlink.jsl.xml.partition;

import io.machinecode.chainlink.jsl.core.inherit.InheritablePlan;
import io.machinecode.chainlink.jsl.xml.XmlProperties;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

import static io.machinecode.chainlink.spi.element.Job.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
//@XmlType(name = "PartitionPlan", propOrder = {
//        "properties"
//})
public class XmlPlan implements XmlStrategy<XmlPlan>, InheritablePlan<XmlPlan, XmlProperties> {

    @XmlAttribute(name = "partitions", required = false)
    private String partitions = ONE;

    @XmlAttribute(name = "threads", required = false)
    private String threads;

    @XmlElement(name = "properties", namespace = NAMESPACE, required = false)
    private List<XmlProperties> properties = new ArrayList<XmlProperties>(0);

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
    public List<XmlProperties> getProperties() {
        return properties;
    }

    public XmlPlan setProperties(final List<XmlProperties> properties) {
        this.properties = properties;
        return this;
    }

    @Override
    public XmlPlan copy() {
        return copy(new XmlPlan());
    }

    @Override
    public XmlPlan copy(final XmlPlan that) {
        return PlanTool.copy(this, that);
        //that.setPartitions(this.partitions);
        //that.setThreads(this.threads);
        //that.setProperties(Util.copy(this.properties));
        //return that;
    }
}
