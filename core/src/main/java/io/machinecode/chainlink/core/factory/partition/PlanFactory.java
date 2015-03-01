package io.machinecode.chainlink.core.factory.partition;

import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.core.expression.PropertyContext;
import io.machinecode.chainlink.core.factory.PropertiesFactory;
import io.machinecode.chainlink.core.jsl.impl.PropertiesImpl;
import io.machinecode.chainlink.core.jsl.impl.partition.PlanImpl;
import io.machinecode.chainlink.spi.jsl.Properties;
import io.machinecode.chainlink.spi.jsl.partition.Plan;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class PlanFactory {

    public static PlanImpl produceExecution(final Plan that, final JobPropertyContext context) {
        final String partitions = Expression.resolveExecutionProperty(that.getPartitions(), context);
        final String threads = Expression.resolveExecutionProperty(that.getThreads() == null ? that.getPartitions() : that.getThreads(), context);
        final List<PropertiesImpl> properties = new ArrayList<PropertiesImpl>(that.getProperties().size());
        for (final Properties x : that.getProperties()) {
            properties.add(PropertiesFactory.produceExecution(x, context));
        }
        return new PlanImpl(partitions, threads, properties);
    }

    public static PlanImpl producePartitioned(final PlanImpl that, final PropertyContext context) {
        final String partitions = Expression.resolvePartitionProperty(that.getPartitions(), context);
        final String threads = Expression.resolvePartitionProperty(that.getThreads(), context);
        final List<PropertiesImpl> properties = new ArrayList<PropertiesImpl>(that.getProperties().size());
        for (final PropertiesImpl x : that.getProperties()) {
            properties.add(PropertiesFactory.producePartitioned(x, context));
        }
        return new PlanImpl(partitions, threads, properties);
    }
}
