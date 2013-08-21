package io.machinecode.nock.core.factory.task;

import io.machinecode.nock.core.descriptor.PropertiesImpl;
import io.machinecode.nock.core.descriptor.task.CheckpointAlgorithmImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.work.partition.CheckpointAlgorithmWork;
import io.machinecode.nock.spi.element.task.CheckpointAlgorithm;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CheckpointAlgorithmFactory implements ElementFactory<CheckpointAlgorithm, CheckpointAlgorithmImpl, CheckpointAlgorithmWork> {

    public static final CheckpointAlgorithmFactory INSTANCE = new CheckpointAlgorithmFactory();

    @Override
    public CheckpointAlgorithmImpl produceDescriptor(final CheckpointAlgorithm that, final JobPropertyContext context) {
        final String ref = Expression.resolveDescriptorProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceDescriptor(that.getProperties(), context);
        return new CheckpointAlgorithmImpl(ref, properties);
    }

    @Override
    public CheckpointAlgorithmWork produceExecution(final CheckpointAlgorithmImpl that, final JobParameterContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        return new CheckpointAlgorithmWork(ref);
    }

    @Override
    public CheckpointAlgorithmWork producePartitioned(final CheckpointAlgorithmWork that, final PartitionPropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        return new CheckpointAlgorithmWork(ref);
    }
}
