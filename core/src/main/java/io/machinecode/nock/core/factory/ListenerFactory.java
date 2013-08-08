package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.model.ListenerImpl;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.jsl.api.Listener;
import io.machinecode.nock.jsl.util.MutablePair;

import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ListenerFactory implements ElementFactory<Listener, ListenerImpl> {

    public static final ListenerFactory INSTANCE = new ListenerFactory();

    @Override
    public ListenerImpl produceBuildTime(final Listener that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> jobProperties = context.getProperties();
        final String ref = Expression.resolveBuildTime(that.getRef(), jobProperties);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceBuildTime(that.getProperties(), context);
        context.addProperties(properties);
        return new ListenerImpl(
            ref,
            properties
        );
    }

    @Override
    public ListenerImpl produceStartTime(final Listener that, final Properties parameters) {
        final String ref = Expression.resolveStartTime(that.getRef(), parameters);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceStartTime(that.getProperties(), parameters);
        return new ListenerImpl(
                ref,
                properties
        );
    }

    @Override
    public ListenerImpl producePartitionTime(final Listener that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> partitionPlan = context.getProperties();
        final String ref = Expression.resolvePartition(that.getRef(), partitionPlan);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitionTime(that.getProperties(), context);
        return new ListenerImpl(
                ref,
                properties
        );
    }
}
