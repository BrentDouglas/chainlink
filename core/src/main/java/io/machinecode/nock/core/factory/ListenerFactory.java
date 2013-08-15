package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.model.ListenerImpl;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.jsl.api.Listener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ListenerFactory implements ElementFactory<Listener, ListenerImpl> {

    public static final ListenerFactory INSTANCE = new ListenerFactory();

    @Override
    public ListenerImpl produceBuildTime(final Listener that, final JobPropertyContext context) {
        final String ref = Expression.resolveBuildTime(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceBuildTime(that.getProperties(), context);
        return new ListenerImpl(
            ref,
            properties
        );
    }


    @Override
    public ListenerImpl producePartitionTime(final Listener that, final PartitionPropertyContext context) {
        final String ref = Expression.resolvePartition(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitionTime(that.getProperties(), context);
        return new ListenerImpl(
                ref,
                properties
        );
    }
}
