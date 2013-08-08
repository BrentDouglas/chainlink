package io.machinecode.nock.core.factory.task;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.task.CheckpointAlgorithmImpl;
import io.machinecode.nock.jsl.api.task.CheckpointAlgorithm;
import io.machinecode.nock.jsl.util.MutablePair;

import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CheckpointAlgorithmFactory implements ElementFactory<CheckpointAlgorithm, CheckpointAlgorithmImpl> {

    public static final CheckpointAlgorithmFactory INSTANCE = new CheckpointAlgorithmFactory();

    @Override
    public CheckpointAlgorithmImpl produceBuildTime(final CheckpointAlgorithm that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> jobProperties = context.getProperties();
        final String ref = Expression.resolveBuildTime(that.getRef(), jobProperties);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceBuildTime(that.getProperties(), context);
        context.addProperties(properties);
        return new CheckpointAlgorithmImpl(ref, properties);
    }

    @Override
    public CheckpointAlgorithmImpl produceStartTime(final CheckpointAlgorithm that, final Properties parameters) {
        final String ref = Expression.resolveStartTime(that.getRef(), parameters);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceStartTime(that.getProperties(), parameters);
        return new CheckpointAlgorithmImpl(ref, properties);
    }

    @Override
    public CheckpointAlgorithmImpl producePartitionTime(final CheckpointAlgorithm that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> partitionPlan = context.getProperties();
        final String ref = Expression.resolvePartition(that.getRef(), partitionPlan);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitionTime(that.getProperties(), context);
        return new CheckpointAlgorithmImpl(ref, properties);
    }
}
