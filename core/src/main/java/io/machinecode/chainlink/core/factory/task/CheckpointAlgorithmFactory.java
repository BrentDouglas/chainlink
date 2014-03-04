package io.machinecode.chainlink.core.factory.task;

import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.factory.PropertiesFactory;
import io.machinecode.chainlink.core.loader.ArtifactReferenceImpl;
import io.machinecode.chainlink.core.model.PropertiesImpl;
import io.machinecode.chainlink.core.model.task.CheckpointAlgorithmImpl;
import io.machinecode.chainlink.spi.element.task.CheckpointAlgorithm;
import io.machinecode.chainlink.spi.factory.ElementFactory;
import io.machinecode.chainlink.spi.factory.JobPropertyContext;
import io.machinecode.chainlink.spi.factory.PropertyContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CheckpointAlgorithmFactory implements ElementFactory<CheckpointAlgorithm, CheckpointAlgorithmImpl> {

    public static final CheckpointAlgorithmFactory INSTANCE = new CheckpointAlgorithmFactory();

    @Override
    public CheckpointAlgorithmImpl produceExecution(final CheckpointAlgorithm that, final JobPropertyContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceExecution(that.getProperties(), context);
        return new CheckpointAlgorithmImpl(
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties
        );
    }

    @Override
    public CheckpointAlgorithmImpl producePartitioned(final CheckpointAlgorithmImpl that, final PropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitioned(that.getProperties(), context);
        return new CheckpointAlgorithmImpl(
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties
        );
    }
}
