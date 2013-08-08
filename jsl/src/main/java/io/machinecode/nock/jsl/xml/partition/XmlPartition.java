package io.machinecode.nock.jsl.xml.partition;

import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.xml.util.Copyable;
import io.machinecode.nock.jsl.xml.util.Util;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import static io.machinecode.nock.jsl.api.Job.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
//@XmlType(name = "Partition", propOrder = {
//        "strategy",
//        "collector",
//        "analyzer",
//        "reducer"
//})
public class XmlPartition implements Copyable<XmlPartition>, Partition {

    @XmlElements({
            @XmlElement(name = "plan", namespace = NAMESPACE, required = false, type = XmlPlan.class),
            @XmlElement(name = "mapper", namespace = NAMESPACE, required = false, type = XmlMapper.class)
    })
    private XmlStrategy strategy;

    @XmlElement(name = "collector", namespace = NAMESPACE, required = false)
    private XmlCollector collector;

    @XmlElement(name = "analyzer", namespace = NAMESPACE, required = false)
    private XmlAnalyser analyzer;

    @XmlElement(name = "reducer", namespace = NAMESPACE, required = false)
    private XmlReducer reducer;

    @Override
    public XmlStrategy getStrategy() {
        return strategy;
    }

    public XmlPartition setStrategy(final XmlStrategy strategy) {
        this.strategy = strategy;
        return this;
    }

    @Override
    public XmlCollector getCollector() {
        return collector;
    }

    public XmlPartition setCollector(final XmlCollector collector) {
        this.collector = collector;
        return this;
    }

    @Override
    public XmlAnalyser getAnalyzer() {
        return analyzer;
    }

    public XmlPartition setAnalyzer(final XmlAnalyser analyzer) {
        this.analyzer = analyzer;
        return this;
    }

    @Override
    public XmlReducer getReducer() {
        return reducer;
    }

    public XmlPartition setReducer(final XmlReducer reducer) {
        this.reducer = reducer;
        return this;
    }

    @Override
    public XmlPartition copy() {
        return copy(new XmlPartition());
    }

    @Override
    public XmlPartition copy(final XmlPartition that) {
        that.setStrategy(Util.copy(this.strategy));
        that.setCollector(Util.copy(this.collector));
        that.setAnalyzer(Util.copy(this.analyzer));
        that.setReducer(Util.copy(this.reducer));
        return that;
    }
}
