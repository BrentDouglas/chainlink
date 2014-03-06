package io.machinecode.chainlink.jsl.fluent.partition;

import io.machinecode.chainlink.jsl.core.inherit.InheritablePartition;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentPartition<T extends FluentStrategy<T>>
        implements InheritablePartition<FluentPartition<T>, FluentStrategy<T>, FluentCollector, FluentAnalyser, FluentReducer> {

    private FluentStrategy<T> strategy;
    private FluentCollector collector;
    private FluentAnalyser analyser;
    private FluentReducer reducer;

    @Override
    public FluentStrategy<T> getStrategy() {
        return this.strategy;
    }

    public FluentPartition<T> setStrategy(final FluentStrategy<T> strategy) {
        this.strategy = strategy;
        return this;
    }

    @Override
    public FluentCollector getCollector() {
        return this.collector;
    }

    @Override
    public FluentPartition<T> setCollector(final FluentCollector collector) {
        this.collector = collector;
        return this;
    }

    @Override
    public FluentAnalyser getAnalyzer() {
        return this.analyser;
    }

    @Override
    public FluentPartition<T> setAnalyzer(final FluentAnalyser analyser) {
        this.analyser = analyser;
        return this;
    }

    @Override
    public FluentReducer getReducer() {
        return this.reducer;
    }

    public FluentPartition<T> setReducer(final FluentReducer reducer) {
        this.reducer = reducer;
        return this;
    }

    @Override
    public FluentPartition<T> copy() {
        return copy(new FluentPartition<T>());
    }

    @Override
    public FluentPartition<T> copy(final FluentPartition<T> that) {
        return PartitionTool.copy(this, that);
    }
}
