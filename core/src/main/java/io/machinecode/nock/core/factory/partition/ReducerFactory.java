package io.machinecode.nock.core.factory.partition;

import io.machinecode.nock.core.descriptor.PropertiesImpl;
import io.machinecode.nock.core.descriptor.partition.ReducerImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.work.partition.ReducerWork;
import io.machinecode.nock.spi.element.partition.Reducer;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ReducerFactory implements ElementFactory<Reducer, ReducerImpl, ReducerWork> {

    public static final ReducerFactory INSTANCE = new ReducerFactory();

    @Override
    public ReducerImpl produceDescriptor(final Reducer that, final JobPropertyContext context) {
        final String ref = Expression.resolveDescriptorProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceDescriptor(that.getProperties(), context);
        return new ReducerImpl(ref, properties);
    }

    @Override
    public ReducerWork produceExecution(final ReducerImpl that, final JobParameterContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        return new ReducerWork(ref);
    }

    @Override
    public ReducerWork producePartitioned(final ReducerWork that, final PartitionPropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        return new ReducerWork(ref);
    }
}
