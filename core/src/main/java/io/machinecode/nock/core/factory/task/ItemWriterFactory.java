package io.machinecode.nock.core.factory.task;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.task.ItemWriterImpl;
import io.machinecode.nock.jsl.api.task.ItemWriter;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemWriterFactory implements ElementFactory<ItemWriter, ItemWriterImpl> {

    public static final ItemWriterFactory INSTANCE = new ItemWriterFactory();

    @Override
    public ItemWriterImpl produceBuildTime(final ItemWriter that, final JobPropertyContext context) {
        final String ref = Expression.resolveBuildTime(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceBuildTime(that.getProperties(), context);
        return new ItemWriterImpl(ref, properties);
    }

    @Override
    public ItemWriterImpl producePartitionTime(final ItemWriter that, final PartitionPropertyContext context) {
        final String ref = Expression.resolvePartition(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitionTime(that.getProperties(), context);
        return new ItemWriterImpl(ref, properties);
    }
}
