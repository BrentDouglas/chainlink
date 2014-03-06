package io.machinecode.chainlink.core.factory.partition;

import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.factory.PropertiesFactory;
import io.machinecode.chainlink.core.element.PropertiesImpl;
import io.machinecode.chainlink.core.element.partition.PlanImpl;
import io.machinecode.chainlink.spi.element.Properties;
import io.machinecode.chainlink.spi.element.partition.Plan;
import io.machinecode.chainlink.core.factory.ElementFactory;
import io.machinecode.chainlink.spi.expression.JobPropertyContext;
import io.machinecode.chainlink.spi.expression.PropertyContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PlanFactory implements ElementFactory<Plan, PlanImpl> {

    public static final PlanFactory INSTANCE = new PlanFactory();

    @Override
    public PlanImpl produceExecution(final Plan that, final JobPropertyContext context) {
        final String partitions = Expression.resolveExecutionProperty(that.getPartitions(), context);
        final String threads = Expression.resolveExecutionProperty(that.getThreads() == null ? that.getPartitions() : that.getThreads(), context);
        final List<PropertiesImpl> properties = new ArrayList<PropertiesImpl>(that.getProperties().size());
        for (final Properties x : that.getProperties()) {
            properties.add(PropertiesFactory.INSTANCE.produceExecution(x, context));
        }
        return new PlanImpl(partitions, threads, properties);
    }

    @Override
    public PlanImpl producePartitioned(final PlanImpl that, final PropertyContext context) {
        final String partitions = Expression.resolvePartitionProperty(that.getPartitions(), context);
        final String threads = Expression.resolvePartitionProperty(that.getThreads(), context);
        final List<PropertiesImpl> properties = new ArrayList<PropertiesImpl>(that.getProperties().size());
        for (final PropertiesImpl x : that.getProperties()) {
            properties.add(PropertiesFactory.INSTANCE.producePartitioned(x, context));
        }
        return new PlanImpl(partitions, threads, properties);
    }
}
