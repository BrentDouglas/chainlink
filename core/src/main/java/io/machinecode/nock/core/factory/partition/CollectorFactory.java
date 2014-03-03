package io.machinecode.nock.core.factory.partition;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.loader.ArtifactReferenceImpl;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.partition.CollectorImpl;
import io.machinecode.nock.spi.element.partition.Collector;
import io.machinecode.nock.spi.factory.ElementFactory;
import io.machinecode.nock.spi.factory.JobPropertyContext;
import io.machinecode.nock.spi.factory.PropertyContext;

import javax.batch.api.partition.PartitionCollector;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CollectorFactory implements ElementFactory<Collector, CollectorImpl> {

    public static final CollectorFactory INSTANCE = new CollectorFactory();

    @Override
    public CollectorImpl produceExecution(final Collector that, final JobPropertyContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceExecution(that.getProperties(), context);
        return new CollectorImpl(
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties
        );
    }

    @Override
    public CollectorImpl producePartitioned(final CollectorImpl that, final PropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitioned(that.getProperties(), context);
        return new CollectorImpl(
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties
        );
    }
}
