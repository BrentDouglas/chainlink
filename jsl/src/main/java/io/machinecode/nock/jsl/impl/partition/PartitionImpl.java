package io.machinecode.nock.jsl.impl.partition;

import io.machinecode.nock.jsl.api.partition.Analyser;
import io.machinecode.nock.jsl.api.partition.Collector;
import io.machinecode.nock.jsl.api.partition.Strategy;
import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.partition.Reducer;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class PartitionImpl<T extends Strategy> implements Partition<T> {

    private final Collector collector;
    private final Analyser analyser;
    private final Reducer reducer;

    public PartitionImpl(final Partition<? extends T> that) {
        this.collector = that.getCollector() == null ? null : new CollectorImpl(that.getCollector());
        this.analyser = that.getAnalyzer() == null ? null : new AnalyserImpl(that.getAnalyzer());
        this.reducer = that.getReducer() == null ? null : new ReducerImpl(that.getReducer());
    }

    @Override
    public Collector getCollector() {
        return this.collector;
    }

    @Override
    public Analyser getAnalyzer() {
        return this.analyser;
    }

    @Override
    public Reducer getReducer() {
         return this.reducer;
    }
}
