package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.model.PropertyImpl;
import io.machinecode.nock.jsl.api.Property;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PropertyFactory implements ElementFactory<Property, PropertyImpl> {

    public static final PropertyFactory INSTANCE = new PropertyFactory();

    @Override
    public PropertyImpl produceBuildTime(final Property that, final JobPropertyContext context) {
        final String name = Expression.resolveBuildTime(that.getName(), context);
        final String value = Expression.resolveBuildTime(that.getValue(), context);
        final PropertyImpl property = new PropertyImpl(name, value);
        context.addProperty(property);
        return property;
    }

    @Override
    public PropertyImpl producePartitionTime(final Property that, final PartitionPropertyContext context) {
        final String name = Expression.resolvePartition(that.getName(), context);
        final String value = Expression.resolvePartition(that.getValue(), context);
        return new PropertyImpl(name, value);
    }
}
