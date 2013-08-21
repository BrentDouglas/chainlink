package io.machinecode.nock.core.work.partition;

import io.machinecode.nock.core.work.Work;
import io.machinecode.nock.spi.element.partition.Partition;
import io.machinecode.nock.spi.element.partition.Strategy;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PartitionWork<T extends Work & Strategy> implements Work, Partition<T> {
    private final CollectorWork collector;
    private final AnalyserWork analyser;
    private final ReducerWork reducer;
    private final T strategy;

    public PartitionWork(final CollectorWork collector, final AnalyserWork analyser,
                         final ReducerWork reducer, final T strategy) {
        this.collector = collector;
        this.analyser = analyser;
        this.reducer = reducer;
        this.strategy = strategy;
    }

    @Override
    public T getStrategy() {
        return this.strategy;
    }

    @Override
    public CollectorWork getCollector() {
        return this.collector;
    }

    @Override
    public AnalyserWork getAnalyzer() {
        return this.analyser;
    }

    @Override
    public ReducerWork getReducer() {
        return this.reducer;
    }
}
