package io.machinecode.nock.core.factory.partition;

import io.machinecode.nock.core.descriptor.PropertiesImpl;
import io.machinecode.nock.core.descriptor.partition.CollectorImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.work.partition.CollectorWork;
import io.machinecode.nock.spi.element.partition.Collector;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CollectorFactory implements ElementFactory<Collector, CollectorImpl, CollectorWork> {

    public static final CollectorFactory INSTANCE = new CollectorFactory();

    @Override
    public CollectorImpl produceDescriptor(final Collector that, final JobPropertyContext context) {
        final String ref = Expression.resolveDescriptorProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceDescriptor(that.getProperties(), context);
        return new CollectorImpl(ref, properties);
    }

    @Override
    public CollectorWork produceExecution(final CollectorImpl that, final JobParameterContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        return new CollectorWork(ref);
    }

    @Override
    public CollectorWork producePartitioned(final CollectorWork that, final PartitionPropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        return new CollectorWork(ref);
    }
}
