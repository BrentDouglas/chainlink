package io.machinecode.chainlink.core.factory.task;

import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.core.expression.PropertyContext;
import io.machinecode.chainlink.core.factory.PropertiesFactory;
import io.machinecode.chainlink.core.inject.ArtifactReferenceImpl;
import io.machinecode.chainlink.core.jsl.impl.PropertiesImpl;
import io.machinecode.chainlink.core.jsl.impl.partition.PartitionImpl;
import io.machinecode.chainlink.core.jsl.impl.task.BatchletImpl;
import io.machinecode.chainlink.spi.jsl.task.Batchlet;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class BatchletFactory {

    public static BatchletImpl produceExecution(final Batchlet that, final PartitionImpl<?> partition, final JobPropertyContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.produceExecution(that.getProperties(), context);
        return new BatchletImpl(
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties,
                partition
        );
    }

    public static BatchletImpl producePartitioned(final BatchletImpl that, final PartitionImpl<?> partition, final PropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.producePartitioned(that.getProperties(), context);
        return new BatchletImpl(
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties,
                partition
        );
    }
}
