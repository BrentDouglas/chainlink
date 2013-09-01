package io.machinecode.nock.core.model.partition;

import io.machinecode.nock.core.loader.TypedArtifactReference;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyReferenceImpl;
import io.machinecode.nock.spi.element.partition.Mapper;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.work.StrategyWork;

import javax.batch.api.partition.PartitionMapper;
import javax.batch.api.partition.PartitionPlan;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MapperImpl extends PropertyReferenceImpl<PartitionMapper> implements Mapper, StrategyWork {

    public MapperImpl(final String ref, final PropertiesImpl properties) {
        super(new TypedArtifactReference<PartitionMapper>(ref, PartitionMapper.class), properties);
    }

    @Override
    public PartitionPlan getPartitionPlan(final InjectionContext context) throws Exception {
        return this.load(context).mapPartitions();
    }
}
