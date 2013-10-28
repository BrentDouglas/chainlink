package io.machinecode.nock.core.factory.task;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.loader.TypedArtifactReference;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.task.CheckpointAlgorithmImpl;
import io.machinecode.nock.spi.element.task.CheckpointAlgorithm;
import io.machinecode.nock.spi.factory.ElementFactory;
import io.machinecode.nock.spi.factory.JobPropertyContext;
import io.machinecode.nock.spi.factory.PropertyContext;

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
                context.getReference(new TypedArtifactReference<javax.batch.api.chunk.CheckpointAlgorithm>(ref, javax.batch.api.chunk.CheckpointAlgorithm.class)),
                properties
        );
    }

    @Override
    public CheckpointAlgorithmImpl producePartitioned(final CheckpointAlgorithmImpl that, final PropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitioned(that.getProperties(), context);
        return new CheckpointAlgorithmImpl(
                context.getReference(new TypedArtifactReference<javax.batch.api.chunk.CheckpointAlgorithm>(ref, javax.batch.api.chunk.CheckpointAlgorithm.class)),
                properties
        );
    }
}
