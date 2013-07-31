package io.machinecode.nock.jsl.xml.partition;

import io.machinecode.nock.jsl.xml.util.Copyable;
import io.machinecode.nock.jsl.xml.util.Util;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import static io.machinecode.nock.jsl.xml.XmlJob.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public class XmlPartition implements Copyable<XmlPartition> {

    @XmlElements({
            @XmlElement(name = "plan", namespace = NAMESPACE, required = false, type = XmlPartitionPlan.class),
            @XmlElement(name = "mapper", namespace = NAMESPACE, required = false, type = XmlPartitionMapper.class)
    })
    private Copyable planOrMapper;

    @XmlElement(name = "collector", namespace = NAMESPACE, required = false)
    private XmlCollector collector;

    @XmlElement(name = "analyzer", namespace = NAMESPACE, required = false)
    private XmlAnalyser analyzer;

    @XmlElement(name = "reducer", namespace = NAMESPACE, required = false)
    private XmlPartitionReducer reducer;

    public Copyable getPlanOrMapper() {
        return planOrMapper;
    }

    public XmlPartitionPlan getPlan() {
        return planOrMapper instanceof XmlPartitionPlan
                ? (XmlPartitionPlan) planOrMapper
                : null;
    }

    public XmlPartitionMapper getMapper() {
        return planOrMapper instanceof XmlPartitionMapper
                ? (XmlPartitionMapper) planOrMapper
                : null;
    }

    public XmlPartition setPlanOrMapper(final Copyable planOrMapper) {
        this.planOrMapper = planOrMapper;
        return this;
    }

    public XmlCollector getCollector() {
        return collector;
    }

    public XmlPartition setCollector(final XmlCollector collector) {
        this.collector = collector;
        return this;
    }

    public XmlAnalyser getAnalyzer() {
        return analyzer;
    }

    public XmlPartition setAnalyzer(final XmlAnalyser analyzer) {
        this.analyzer = analyzer;
        return this;
    }

    public XmlPartitionReducer getReducer() {
        return reducer;
    }

    public XmlPartition setReducer(final XmlPartitionReducer reducer) {
        this.reducer = reducer;
        return this;
    }

    @Override
    public XmlPartition copy() {
        return copy(new XmlPartition());
    }

    @Override
    public XmlPartition copy(final XmlPartition that) {
        that.setPlanOrMapper(Util.copy(this.planOrMapper));
        that.setCollector(Util.copy(this.collector));
        that.setAnalyzer(Util.copy(this.analyzer));
        that.setReducer(Util.copy(this.reducer));
        return that;
    }
}
