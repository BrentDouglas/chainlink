package io.machinecode.chainlink.core.factory;

import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.core.expression.PartitionPropertyContext;
import io.machinecode.chainlink.core.jsl.impl.PropertyImpl;
import io.machinecode.chainlink.spi.jsl.Property;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class PropertyFactory {

    public static PropertyImpl produceExecution(final Property that, final JobPropertyContext context) {
        final String name = Expression.resolveExecutionProperty(that.getName(), context);
        final String value = Expression.resolveExecutionProperty(that.getValue(), context);
        final PropertyImpl property = new PropertyImpl(name, value);
        context.addProperty(property);
        return property;
    }

    public static PropertyImpl producePartitioned(final PropertyImpl that, final PartitionPropertyContext context) {
        final String name = Expression.resolvePartitionProperty(that.getName(), context);
        final String value = Expression.resolvePartitionProperty(that.getValue(), context);
        return new PropertyImpl(name, value);
    }
}
