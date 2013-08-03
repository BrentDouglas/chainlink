package io.machinecode.nock.jsl.impl.partition;

import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.partition.Mapper;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MapperPartitionImpl extends PartitionImpl<Mapper> implements Partition<Mapper> {

    private final Mapper mapper;

    public MapperPartitionImpl(final Partition<? extends Mapper> that) {
        super(that);
        this.mapper = that.getStrategy() == null ? null : new MapperImpl(that.getStrategy());
    }

    @Override
    public Mapper getStrategy() {
        return this.mapper;
    }
}
