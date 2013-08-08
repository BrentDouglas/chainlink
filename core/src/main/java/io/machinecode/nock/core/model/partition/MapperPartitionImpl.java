package io.machinecode.nock.core.model.partition;

import io.machinecode.nock.jsl.api.partition.Analyser;
import io.machinecode.nock.jsl.api.partition.Collector;
import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.partition.Mapper;
import io.machinecode.nock.jsl.api.partition.Reducer;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MapperPartitionImpl extends PartitionImpl<Mapper> implements Partition<Mapper> {

    private final Mapper mapper;

    public MapperPartitionImpl(final Collector collector, final Analyser analyser, final Reducer reducer, final Mapper mapper) {
        super(collector, analyser, reducer);
        this.mapper = mapper;
    }

    @Override
    public Mapper getStrategy() {
        return this.mapper;
    }
}
