package io.machinecode.nock.core.factory.task;

import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.task.ItemProcessorImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.spi.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.spi.element.task.ItemProcessor;
import io.machinecode.nock.spi.factory.JobPropertyContext;
import io.machinecode.nock.spi.factory.PropertyContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemProcessorFactory implements ElementFactory<ItemProcessor, ItemProcessorImpl> {

    public static final ItemProcessorFactory INSTANCE = new ItemProcessorFactory();

    @Override
    public ItemProcessorImpl produceExecution(final ItemProcessor that, final JobPropertyContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceExecution(that.getProperties(), context);
        return new ItemProcessorImpl(ref, properties);
    }

    @Override
    public ItemProcessorImpl producePartitioned(final ItemProcessorImpl that, final PropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitioned(that.getProperties(), context);
        return new ItemProcessorImpl(ref, properties);
    }
}
