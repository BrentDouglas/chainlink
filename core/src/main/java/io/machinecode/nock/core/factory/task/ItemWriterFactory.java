package io.machinecode.nock.core.factory.task;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.task.ItemWriterImpl;
import io.machinecode.nock.jsl.api.task.ItemWriter;
import io.machinecode.nock.jsl.util.MutablePair;

import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemWriterFactory implements ElementFactory<ItemWriter, ItemWriterImpl> {

    public static final ItemWriterFactory INSTANCE = new ItemWriterFactory();

    @Override
    public ItemWriterImpl produceBuildTime(final ItemWriter that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> jobProperties = context.getProperties();
        final String ref = Expression.resolveBuildTime(that.getRef(), jobProperties);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceBuildTime(that.getProperties(), context);
        context.addProperties(properties);
        return new ItemWriterImpl(ref, properties);
    }

    @Override
    public ItemWriterImpl produceStartTime(final ItemWriter that, final Properties parameters) {
        final String ref = Expression.resolveStartTime(that.getRef(), parameters);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceStartTime(that.getProperties(), parameters);
        return new ItemWriterImpl(ref, properties);
    }

    @Override
    public ItemWriterImpl producePartitionTime(final ItemWriter that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> partitionPlan = context.getProperties();
        final String ref = Expression.resolvePartition(that.getRef(), partitionPlan);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitionTime(that.getProperties(), context);
        return new ItemWriterImpl(ref, properties);
    }
}
