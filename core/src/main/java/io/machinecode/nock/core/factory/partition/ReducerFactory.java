package io.machinecode.nock.core.factory.partition;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.partition.ReducerImpl;
import io.machinecode.nock.jsl.api.partition.Reducer;
import io.machinecode.nock.jsl.util.MutablePair;

import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ReducerFactory implements ElementFactory<Reducer, ReducerImpl> {

    public static final ReducerFactory INSTANCE = new ReducerFactory();

    @Override
    public ReducerImpl produceBuildTime(final Reducer that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> jobProperties = context.getProperties();
        final String ref = Expression.resolveBuildTime(that.getRef(), jobProperties);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceBuildTime(that.getProperties(), context);
        context.addProperties(properties);
        return new ReducerImpl(ref, properties);
    }

    @Override
    public ReducerImpl produceStartTime(final Reducer that, final Properties parameters) {
        final String ref = Expression.resolveStartTime(that.getRef(), parameters);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceStartTime(that.getProperties(), parameters);
        return new ReducerImpl(ref, properties);
    }

    @Override
    public ReducerImpl producePartitionTime(final Reducer that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> partitionPlan = context.getProperties();
        final String ref = Expression.resolvePartition(that.getRef(), partitionPlan);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitionTime(that.getProperties(), context);
        return new ReducerImpl(ref, properties);
    }
}
