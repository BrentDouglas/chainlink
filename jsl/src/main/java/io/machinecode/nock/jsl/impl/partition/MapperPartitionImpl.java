package io.machinecode.nock.jsl.impl.partition;

import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.partition.PartitionMapper;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MapperPartitionImpl extends PartitionImpl<PartitionMapper> implements Partition<PartitionMapper> {

    private final PartitionMapper mapper;

    public MapperPartitionImpl(final Partition<? extends PartitionMapper> that) {
        super(that);
        this.mapper = that.getMapper() == null ? null : new PartitionMapperImpl(that.getMapper());
    }

    @Override
    public PartitionMapper getMapper() {
        return this.mapper;
    }
}
