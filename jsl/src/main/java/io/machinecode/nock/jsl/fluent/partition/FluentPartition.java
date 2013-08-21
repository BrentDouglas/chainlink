package io.machinecode.nock.jsl.fluent.partition;

import io.machinecode.nock.spi.element.partition.Analyser;
import io.machinecode.nock.spi.element.partition.Collector;
import io.machinecode.nock.spi.element.partition.Strategy;
import io.machinecode.nock.spi.element.partition.Partition;
import io.machinecode.nock.spi.element.partition.Reducer;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class FluentPartition<T extends Strategy> implements Partition<T> {

    private T strategy;
    private Collector collector;
    private Analyser analyser;
    private Reducer reducer;

    @Override
    public T getStrategy() {
        return this.strategy;
    }

    public FluentPartition<T> setStrategy(final T strategy) {
        this.strategy = strategy;
        return this;
    }

    @Override
    public Collector getCollector() {
        return this.collector;
    }

    public FluentPartition<T> setAnalyser(final Analyser analyser) {
        this.analyser = analyser;
        return this;
    }

    @Override
    public Analyser getAnalyzer() {
        return this.analyser;
    }

    public FluentPartition<T> setCollector(final Collector collector) {
        this.collector = collector;
        return this;
    }

    @Override
    public Reducer getReducer() {
        return this.reducer;
    }

    public FluentPartition<T> setReducer(final Reducer reducer) {
        this.reducer = reducer;
        return this;
    }
}
