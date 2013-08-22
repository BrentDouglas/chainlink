package io.machinecode.nock.core.model.partition;

import io.machinecode.nock.spi.element.partition.Partition;
import io.machinecode.nock.spi.element.partition.Strategy;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PartitionImpl<T extends Strategy> implements Partition<T> {

    private final CollectorImpl collector;
    private final AnalyserImpl analyser;
    private final ReducerImpl reducer;
    private final T strategy;

    public PartitionImpl(final CollectorImpl collector, final AnalyserImpl analyser, final ReducerImpl reducer, final T strategy) {
        this.collector = collector;
        this.analyser = analyser;
        this.reducer = reducer;
        this.strategy = strategy;
    }

    @Override
    public CollectorImpl getCollector() {
        return this.collector;
    }

    @Override
    public AnalyserImpl getAnalyzer() {
        return this.analyser;
    }

    @Override
    public ReducerImpl getReducer() {
         return this.reducer;
    }

    @Override
    public T getStrategy() {
        return this.strategy;
    }
}
