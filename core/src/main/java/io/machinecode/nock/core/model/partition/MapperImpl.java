package io.machinecode.nock.core.model.partition;

import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyReferenceImpl;
import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.spi.element.partition.Mapper;

import javax.batch.api.partition.PartitionMapper;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MapperImpl extends PropertyReferenceImpl<PartitionMapper> implements Mapper {

    public MapperImpl(final String ref, final PropertiesImpl properties) {
        super(new ResolvableReference<PartitionMapper>(ref, PartitionMapper.class), properties);
    }
}
