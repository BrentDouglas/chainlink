package io.machinecode.nock.core.factory.task;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.descriptor.PropertiesImpl;
import io.machinecode.nock.core.descriptor.task.ItemProcessorImpl;
import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.core.work.task.ItemProcessorWork;
import io.machinecode.nock.spi.element.task.ItemProcessor;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemProcessorFactory implements ElementFactory<ItemProcessor, ItemProcessorImpl, ItemProcessorWork> {

    public static final ItemProcessorFactory INSTANCE = new ItemProcessorFactory();

    @Override
    public ItemProcessorImpl produceDescriptor(final ItemProcessor that, final JobPropertyContext context) {
        final String ref = Expression.resolveDescriptorProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceDescriptor(that.getProperties(), context);
        return new ItemProcessorImpl(ref, properties);
    }

    @Override
    public ItemProcessorWork produceExecution(final ItemProcessorImpl that, final JobParameterContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        return new ItemProcessorWork(new ResolvableReference<javax.batch.api.chunk.ItemProcessor>(ref, javax.batch.api.chunk.ItemProcessor.class));
    }

    @Override
    public ItemProcessorWork producePartitioned(final ItemProcessorWork that, final PartitionPropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        return new ItemProcessorWork(new ResolvableReference<javax.batch.api.chunk.ItemProcessor>(ref, javax.batch.api.chunk.ItemProcessor.class));
    }
}
