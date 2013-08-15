package io.machinecode.nock.core.factory.partition;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.partition.ReducerImpl;
import io.machinecode.nock.jsl.api.partition.Reducer;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ReducerFactory implements ElementFactory<Reducer, ReducerImpl> {

    public static final ReducerFactory INSTANCE = new ReducerFactory();

    @Override
    public ReducerImpl produceBuildTime(final Reducer that, final JobPropertyContext context) {
        final String ref = Expression.resolveBuildTime(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceBuildTime(that.getProperties(), context);
        return new ReducerImpl(ref, properties);
    }

    @Override
    public ReducerImpl producePartitionTime(final Reducer that, final PartitionPropertyContext context) {
        final String ref = Expression.resolvePartition(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitionTime(that.getProperties(), context);
        return new ReducerImpl(ref, properties);
    }
}
