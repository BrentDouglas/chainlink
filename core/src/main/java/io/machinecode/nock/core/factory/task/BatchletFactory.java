package io.machinecode.nock.core.factory.task;

import io.machinecode.nock.core.descriptor.PropertiesImpl;
import io.machinecode.nock.core.descriptor.task.BatchletImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.work.task.BatchletWork;
import io.machinecode.nock.spi.element.task.Batchlet;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class BatchletFactory implements ElementFactory<Batchlet, BatchletImpl, BatchletWork> {

    public static final BatchletFactory INSTANCE = new BatchletFactory();

    @Override
    public BatchletImpl produceDescriptor(final Batchlet that, final JobPropertyContext context) {
        final String ref = Expression.resolveDescriptorProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceDescriptor(that.getProperties(), context);
        return new BatchletImpl(ref, properties);
    }

    @Override
    public BatchletWork produceExecution(final BatchletImpl that, final JobParameterContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        return new BatchletWork(ref);
    }

    @Override
    public BatchletWork producePartitioned(final BatchletWork that, final PartitionPropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        return new BatchletWork(ref);
    }
}
