package io.machinecode.nock.jsl.impl.partition;

import io.machinecode.nock.jsl.api.partition.Analyser;
import io.machinecode.nock.jsl.api.partition.Collector;
import io.machinecode.nock.jsl.api.partition.Mapper;
import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.partition.PartitionReducer;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class PartitionImpl<T extends Mapper> implements Partition<T> {

    private final Collector collector;
    private final Analyser analyser;
    private final PartitionReducer reducer;

    public PartitionImpl(final Partition<T> that) {
        this.collector = new CollectorImpl(that.getCollector());
        this.analyser = new AnalyserImpl(that.getAnalyzer());
        this.reducer = new PartitionReducerImpl(that.getReducer());
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
    public PartitionReducer getReducer() {
         return this.reducer;
    }
}
