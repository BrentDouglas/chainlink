package io.machinecode.nock.core.factory.task;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.loader.TypedArtifactReference;
import io.machinecode.nock.core.model.ListenersImpl;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.partition.PartitionImpl;
import io.machinecode.nock.core.model.task.BatchletImpl;
import io.machinecode.nock.spi.element.task.Batchlet;
import io.machinecode.nock.spi.factory.JobPropertyContext;
import io.machinecode.nock.spi.factory.PropertyContext;
import io.machinecode.nock.spi.factory.TaskFactory;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class BatchletFactory implements TaskFactory<Batchlet, BatchletImpl, ListenersImpl, PartitionImpl<?>> {

    public static final BatchletFactory INSTANCE = new BatchletFactory();

    @Override
    public BatchletImpl produceExecution(final Batchlet that, final ListenersImpl _, PartitionImpl<?> partition, final JobPropertyContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceExecution(that.getProperties(), context);
        return new BatchletImpl(
                context.getReference(new TypedArtifactReference<javax.batch.api.Batchlet>(ref, javax.batch.api.Batchlet.class)),
                properties,
                partition
        );
    }

    @Override
    public BatchletImpl producePartitioned(final BatchletImpl that, final ListenersImpl _, PartitionImpl<?> partition, final PropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitioned(that.getProperties(), context);
        return new BatchletImpl(
                context.getReference(new TypedArtifactReference<javax.batch.api.Batchlet>(ref, javax.batch.api.Batchlet.class)),
                properties,
                partition
        );
    }
}
