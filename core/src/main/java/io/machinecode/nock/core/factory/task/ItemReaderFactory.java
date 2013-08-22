package io.machinecode.nock.core.factory.task;

import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.task.ItemReaderImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.spi.element.task.ItemReader;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemReaderFactory implements ElementFactory<ItemReader, ItemReaderImpl> {

    public static final ItemReaderFactory INSTANCE = new ItemReaderFactory();

    @Override
    public ItemReaderImpl produceExecution(final ItemReader that, final JobPropertyContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceExecution(that.getProperties(), context);
        return new ItemReaderImpl(ref, properties);
    }

    @Override
    public ItemReaderImpl producePartitioned(final ItemReaderImpl that, final PartitionPropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitioned(that.getProperties(), context);
        return new ItemReaderImpl(ref, properties);
    }
}
