package io.machinecode.nock.jsl.fluent.partition;

import io.machinecode.nock.jsl.api.partition.Analyser;
import io.machinecode.nock.jsl.api.partition.Collector;
import io.machinecode.nock.jsl.api.partition.Mapper;
import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.partition.PartitionReducer;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class FluentPartition<T extends Mapper> implements Partition<T> {

    private T mapper;
    private Collector collector;
    private Analyser analyser;
    private PartitionReducer reducer;

    @Override
    public T getMapper() {
        return this.mapper;
    }

    public FluentPartition<T> setMapper(final T mapper) {
        this.mapper = mapper;
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
    public PartitionReducer getReducer() {
        return this.reducer;
    }

    public FluentPartition<T> setReducer(final PartitionReducer reducer) {
        this.reducer = reducer;
        return this;
    }
}
