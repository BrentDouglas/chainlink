package io.machinecode.nock.core.factory.partition;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.partition.PlanImpl;
import io.machinecode.nock.jsl.api.partition.Plan;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PlanFactory implements ElementFactory<Plan, PlanImpl> {

    public static final PlanFactory INSTANCE = new PlanFactory();

    @Override
    public PlanImpl produceBuildTime(final Plan that, final JobPropertyContext context) {
        final String partitions = Expression.resolveBuildTime(that.getPartitions(), context);
        final String threads = Expression.resolveBuildTime(that.getThreads() == null ? that.getPartitions() : that.getThreads(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceBuildTime(that.getProperties(), context);
        return new PlanImpl(partitions, threads, properties);
    }

    @Override
    public PlanImpl producePartitionTime(final Plan that, final PartitionPropertyContext context) {
        final String partitions = Expression.resolvePartition(that.getPartitions(), context);
        final String threads = Expression.resolvePartition(that.getThreads(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitionTime(that.getProperties(), context);
        return new PlanImpl(partitions, threads, properties);
    }
}
