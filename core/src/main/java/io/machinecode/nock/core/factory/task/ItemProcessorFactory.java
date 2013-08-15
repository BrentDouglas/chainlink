package io.machinecode.nock.core.factory.task;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.task.ItemProcessorImpl;
import io.machinecode.nock.jsl.api.task.ItemProcessor;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemProcessorFactory implements ElementFactory<ItemProcessor, ItemProcessorImpl> {

    public static final ItemProcessorFactory INSTANCE = new ItemProcessorFactory();

    @Override
    public ItemProcessorImpl produceBuildTime(final ItemProcessor that, final JobPropertyContext context) {
        final String ref = Expression.resolveBuildTime(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceBuildTime(that.getProperties(), context);
        return new ItemProcessorImpl(ref, properties);
    }

    @Override
    public ItemProcessorImpl producePartitionTime(final ItemProcessor that, final PartitionPropertyContext context) {
        final String ref = Expression.resolvePartition(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitionTime(that.getProperties(), context);
        return new ItemProcessorImpl(ref, properties);
    }
}
