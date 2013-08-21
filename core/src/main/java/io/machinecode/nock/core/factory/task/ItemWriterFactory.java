package io.machinecode.nock.core.factory.task;

import io.machinecode.nock.core.descriptor.PropertiesImpl;
import io.machinecode.nock.core.descriptor.task.ItemWriterImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.core.work.task.ItemWriterWork;
import io.machinecode.nock.spi.element.task.ItemWriter;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemWriterFactory implements ElementFactory<ItemWriter, ItemWriterImpl, ItemWriterWork> {

    public static final ItemWriterFactory INSTANCE = new ItemWriterFactory();

    @Override
    public ItemWriterImpl produceDescriptor(final ItemWriter that, final JobPropertyContext context) {
        final String ref = Expression.resolveDescriptorProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceDescriptor(that.getProperties(), context);
        return new ItemWriterImpl(ref, properties);
    }

    @Override
    public ItemWriterWork produceExecution(final ItemWriterImpl that, final JobParameterContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        return new ItemWriterWork(new ResolvableReference<javax.batch.api.chunk.ItemWriter>(ref, javax.batch.api.chunk.ItemWriter.class));
    }

    @Override
    public ItemWriterWork producePartitioned(final ItemWriterWork that, final PartitionPropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        return new ItemWriterWork(new ResolvableReference<javax.batch.api.chunk.ItemWriter>(ref, javax.batch.api.chunk.ItemWriter.class));
    }
}
