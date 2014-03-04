package io.machinecode.chainlink.core.factory;

import io.machinecode.chainlink.core.model.PropertyImpl;
import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.spi.factory.ElementFactory;
import io.machinecode.chainlink.spi.element.Property;
import io.machinecode.chainlink.spi.factory.JobPropertyContext;
import io.machinecode.chainlink.spi.factory.PropertyContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
