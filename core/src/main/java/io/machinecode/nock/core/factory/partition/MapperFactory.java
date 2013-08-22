package io.machinecode.nock.core.factory.partition;

import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.partition.MapperImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.spi.element.partition.Mapper;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MapperFactory implements ElementFactory<Mapper, MapperImpl> {

    public static final MapperFactory INSTANCE = new MapperFactory();

    @Override
    public MapperImpl produceExecution(final Mapper that, final JobPropertyContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceExecution(that.getProperties(), context);
        return new MapperImpl(ref, properties);
    }

    @Override
    public MapperImpl producePartitioned(final MapperImpl that, final PartitionPropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitioned(that.getProperties(), context);
        return new MapperImpl(ref, properties);
    }
}
