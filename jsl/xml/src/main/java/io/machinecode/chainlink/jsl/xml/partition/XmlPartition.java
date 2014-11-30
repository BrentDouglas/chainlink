package io.machinecode.chainlink.jsl.xml.partition;

import io.machinecode.chainlink.jsl.core.inherit.InheritablePartition;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import static io.machinecode.chainlink.spi.element.Job.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
@XmlAccessorType(NONE)
//@XmlType(name = "Partition", propOrder = {
//        "strategy",
//        "collector",
//        "analyzer",
//        "reducer"
//})
public class XmlPartition implements InheritablePartition<XmlPartition, XmlStrategy, XmlCollector, XmlAnalyser, XmlReducer> {

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
        return PartitionTool.copy(this, that);
    }
}
