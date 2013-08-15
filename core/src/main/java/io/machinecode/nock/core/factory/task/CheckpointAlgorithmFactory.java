package io.machinecode.nock.core.factory.task;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.task.CheckpointAlgorithmImpl;
import io.machinecode.nock.jsl.api.task.CheckpointAlgorithm;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CheckpointAlgorithmFactory implements ElementFactory<CheckpointAlgorithm, CheckpointAlgorithmImpl> {

    public static final CheckpointAlgorithmFactory INSTANCE = new CheckpointAlgorithmFactory();

    @Override
    public CheckpointAlgorithmImpl produceBuildTime(final CheckpointAlgorithm that, final JobPropertyContext context) {
        final String ref = Expression.resolveBuildTime(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceBuildTime(that.getProperties(), context);
        return new CheckpointAlgorithmImpl(ref, properties);
    }

    @Override
    public CheckpointAlgorithmImpl producePartitionTime(final CheckpointAlgorithm that, final PartitionPropertyContext context) {
        final String ref = Expression.resolvePartition(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitionTime(that.getProperties(), context);
        return new CheckpointAlgorithmImpl(ref, properties);
    }
}
