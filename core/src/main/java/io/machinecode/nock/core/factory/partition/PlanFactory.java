package io.machinecode.nock.core.factory.partition;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.descriptor.PropertiesImpl;
import io.machinecode.nock.core.descriptor.partition.PlanImpl;
import io.machinecode.nock.core.work.partition.PlanWork;
import io.machinecode.nock.spi.element.partition.Plan;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PlanFactory implements ElementFactory<Plan, PlanImpl, PlanWork> {

    public static final PlanFactory INSTANCE = new PlanFactory();

    @Override
    public PlanImpl produceDescriptor(final Plan that, final JobPropertyContext context) {
        final String partitions = Expression.resolveDescriptorProperty(that.getPartitions(), context);
        final String threads = Expression.resolveDescriptorProperty(that.getThreads() == null ? that.getPartitions() : that.getThreads(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceDescriptor(that.getProperties(), context);
        return new PlanImpl(partitions, threads, properties);
    }

    @Override
    public PlanWork produceExecution(final PlanImpl that, final JobParameterContext context) {
        final String partitions = Expression.resolveExecutionProperty(that.getPartitions(), context);
        final String threads = Expression.resolveExecutionProperty(that.getThreads() == null ? that.getPartitions() : that.getThreads(), context);
        return new PlanWork(partitions, threads);
    }

    @Override
    public PlanWork producePartitioned(final PlanWork that, final PartitionPropertyContext context) {
        final String partitions = Expression.resolvePartitionProperty(that.getPartitions(), context);
        final String threads = Expression.resolvePartitionProperty(that.getThreads(), context);
        return new PlanWork(partitions, threads);
    }
}
