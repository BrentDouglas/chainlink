package io.machinecode.nock.core.factory.partition;

import io.machinecode.nock.core.descriptor.PropertiesImpl;
import io.machinecode.nock.core.descriptor.partition.MapperImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.work.partition.MapperWork;
import io.machinecode.nock.spi.element.partition.Mapper;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MapperFactory implements ElementFactory<Mapper, MapperImpl, MapperWork> {

    public static final MapperFactory INSTANCE = new MapperFactory();

    @Override
    public MapperImpl produceDescriptor(final Mapper that, final JobPropertyContext context) {
        final String ref = Expression.resolveDescriptorProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceDescriptor(that.getProperties(), context);
        return new MapperImpl(ref, properties);
    }

    @Override
    public MapperWork produceExecution(final MapperImpl that, final JobParameterContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        return new MapperWork(ref);
    }

    @Override
    public MapperWork producePartitioned(final MapperWork that, final PartitionPropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        return new MapperWork(ref);
    }
}
