package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.descriptor.PropertyImpl;
import io.machinecode.nock.spi.element.Property;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PropertyFactory implements ElementFactory<Property, PropertyImpl, Object> {

    public static final PropertyFactory INSTANCE = new PropertyFactory();

    @Override
    public PropertyImpl produceDescriptor(final Property that, final JobPropertyContext context) {
        final String name = Expression.resolveDescriptorProperty(that.getName(), context);
        final String value = Expression.resolveDescriptorProperty(that.getValue(), context);
        final PropertyImpl property = new PropertyImpl(name, value);
        context.addProperty(property);
        return property;
    }

    @Override
    public Object produceExecution(final PropertyImpl that, final JobParameterContext context) {
        throw new NotImplementedException();
    }

    @Override
    public Object producePartitioned(final Object that, final PartitionPropertyContext context) {
        throw new NotImplementedException();
    }
}
