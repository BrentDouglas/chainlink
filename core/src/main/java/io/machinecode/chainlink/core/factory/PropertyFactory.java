package io.machinecode.chainlink.core.factory;

import io.machinecode.chainlink.core.element.PropertyImpl;
import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.spi.element.Property;
import io.machinecode.chainlink.spi.expression.JobPropertyContext;
import io.machinecode.chainlink.spi.expression.PropertyContext;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class PropertyFactory implements ElementFactory<Property, PropertyImpl> {

    public static final PropertyFactory INSTANCE = new PropertyFactory();

    @Override
    public PropertyImpl produceExecution(final Property that, final JobPropertyContext context) {
        final String name = Expression.resolveExecutionProperty(that.getName(), context);
        final String value = Expression.resolveExecutionProperty(that.getValue(), context);
        final PropertyImpl property = new PropertyImpl(name, value);
        context.addProperty(property);
        return property;
    }

    @Override
    public PropertyImpl producePartitioned(final PropertyImpl that, final PropertyContext context) {
        final String name = Expression.resolvePartitionProperty(that.getName(), context);
        final String value = Expression.resolvePartitionProperty(that.getValue(), context);
        return new PropertyImpl(name, value);
    }
}
