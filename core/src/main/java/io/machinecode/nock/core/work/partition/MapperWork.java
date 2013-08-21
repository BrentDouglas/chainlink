package io.machinecode.nock.core.work.partition;

import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.core.work.Work;
import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.partition.Mapper;

import javax.batch.api.partition.PartitionMapper;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MapperWork implements Work, Mapper {

    private final ResolvableReference<PartitionMapper> mapper;

    public MapperWork(final String ref) {
        this.mapper = new ResolvableReference<PartitionMapper>(ref, PartitionMapper.class);
    }

    @Override
    public String getRef() {
        return this.mapper.ref();
    }

    @Override
    public Properties getProperties() {
        return null;
    }
}
