package io.machinecode.nock.core.factory.task;

import io.machinecode.nock.core.descriptor.PropertiesImpl;
import io.machinecode.nock.core.descriptor.task.ItemReaderImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.core.work.task.ItemReaderWork;
import io.machinecode.nock.spi.element.task.ItemReader;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemReaderFactory implements ElementFactory<ItemReader, ItemReaderImpl, ItemReaderWork> {

    public static final ItemReaderFactory INSTANCE = new ItemReaderFactory();

    @Override
    public ItemReaderImpl produceDescriptor(final ItemReader that, final JobPropertyContext context) {
        final String ref = Expression.resolveDescriptorProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceDescriptor(that.getProperties(), context);
        return new ItemReaderImpl(ref, properties);
    }

    @Override
    public ItemReaderWork produceExecution(final ItemReaderImpl that, final JobParameterContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        return new ItemReaderWork(new ResolvableReference<javax.batch.api.chunk.ItemReader>(ref, javax.batch.api.chunk.ItemReader.class));
    }

    @Override
    public ItemReaderWork producePartitioned(final ItemReaderWork that, final PartitionPropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        return new ItemReaderWork(new ResolvableReference<javax.batch.api.chunk.ItemReader>(ref, javax.batch.api.chunk.ItemReader.class));
    }
}
