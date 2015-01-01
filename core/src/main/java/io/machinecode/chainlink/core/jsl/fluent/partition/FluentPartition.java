package io.machinecode.chainlink.core.jsl.fluent.partition;

import io.machinecode.chainlink.spi.jsl.inherit.InheritablePartition;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentPartition
        implements InheritablePartition<FluentPartition, FluentStrategy, FluentCollector, FluentAnalyser, FluentReducer> {

    private FluentStrategy strategy;
    private FluentCollector collector;
    private FluentAnalyser analyser;
    private FluentReducer reducer;

    @Override
    public FluentStrategy getStrategy() {
        return this.strategy;
    }

    public FluentPartition setStrategy(final FluentStrategy strategy) {
        this.strategy = strategy;
        return this;
    }

    public FluentPartition setMapper(final FluentMapper strategy) {
        this.strategy = strategy;
        return this;
    }

    public FluentPartition setPlan(final FluentPlan strategy) {
        this.strategy = strategy;
        return this;
    }

    @Override
    public FluentCollector getCollector() {
        return this.collector;
    }

    @Override
    public FluentPartition setCollector(final FluentCollector collector) {
        this.collector = collector;
        return this;
    }

    @Override
    public FluentAnalyser getAnalyzer() {
        return this.analyser;
    }

    @Override
    public FluentPartition setAnalyzer(final FluentAnalyser analyser) {
        this.analyser = analyser;
        return this;
    }

    @Override
    public FluentReducer getReducer() {
        return this.reducer;
    }

    public FluentPartition setReducer(final FluentReducer reducer) {
        this.reducer = reducer;
        return this;
    }

    @Override
    public FluentPartition copy() {
        return copy(new FluentPartition());
    }

    @Override
    public FluentPartition copy(final FluentPartition that) {
        return PartitionTool.copy(this, that);
    }
}
