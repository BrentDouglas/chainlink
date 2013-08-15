package io.machinecode.nock.core.factory.partition;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.partition.CollectorImpl;
import io.machinecode.nock.jsl.api.partition.Collector;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CollectorFactory implements ElementFactory<Collector, CollectorImpl> {

    public static final CollectorFactory INSTANCE = new CollectorFactory();

    @Override
    public CollectorImpl produceBuildTime(final Collector that, final JobPropertyContext context) {
        final String ref = Expression.resolveBuildTime(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceBuildTime(that.getProperties(), context);
        return new CollectorImpl(ref, properties);
    }

    @Override
    public CollectorImpl producePartitionTime(final Collector that, final PartitionPropertyContext context) {
        final String ref = Expression.resolvePartition(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitionTime(that.getProperties(), context);
        return new CollectorImpl(ref, properties);
    }
}
