package io.machinecode.chainlink.core.factory.task;

import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.factory.PropertiesFactory;
import io.machinecode.chainlink.core.loader.ArtifactReferenceImpl;
import io.machinecode.chainlink.core.model.ListenersImpl;
import io.machinecode.chainlink.core.model.PropertiesImpl;
import io.machinecode.chainlink.core.model.partition.PartitionImpl;
import io.machinecode.chainlink.core.model.task.BatchletImpl;
import io.machinecode.chainlink.spi.element.task.Batchlet;
import io.machinecode.chainlink.spi.factory.JobPropertyContext;
import io.machinecode.chainlink.spi.factory.PropertyContext;
import io.machinecode.chainlink.spi.factory.TaskFactory;

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
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties,
                partition
        );
    }

    @Override
    public BatchletImpl producePartitioned(final BatchletImpl that, final ListenersImpl _, PartitionImpl<?> partition, final PropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitioned(that.getProperties(), context);
        return new BatchletImpl(
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties,
                partition
        );
    }
}
