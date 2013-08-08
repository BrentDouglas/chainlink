package io.machinecode.nock.core.factory.partition;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.partition.PlanImpl;
import io.machinecode.nock.jsl.api.partition.Plan;
import io.machinecode.nock.jsl.util.MutablePair;

import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PlanFactory implements ElementFactory<Plan, PlanImpl> {

    public static final PlanFactory INSTANCE = new PlanFactory();

    @Override
    public PlanImpl produceBuildTime(final Plan that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> jobProperties = context.getProperties();
        final String partitions = Expression.resolveBuildTime(that.getPartitions(), jobProperties);
        final String threads = Expression.resolveBuildTime(that.getThreads() == null ? that.getPartitions() : that.getThreads(), jobProperties);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceBuildTime(that.getProperties(), context);
        context.addProperties(properties);
        return new PlanImpl(partitions, threads, properties);
    }

    @Override
    public PlanImpl produceStartTime(final Plan that, final Properties parameters) {
        final String partitions = Expression.resolveStartTime(that.getPartitions(), parameters);
        final String threads = Expression.resolveStartTime(that.getThreads(), parameters);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceStartTime(that.getProperties(), parameters);
        return new PlanImpl(partitions, threads, properties);
    }

    @Override
    public PlanImpl producePartitionTime(final Plan that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> partitionPlan = context.getProperties();
        final String partitions = Expression.resolvePartition(that.getPartitions(), partitionPlan);
        final String threads = Expression.resolvePartition(that.getThreads(), partitionPlan);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitionTime(that.getProperties(), context);
        return new PlanImpl(partitions, threads, properties);
    }
}
