package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.descriptor.PropertiesImpl;
import io.machinecode.nock.core.descriptor.PropertyImpl;
import io.machinecode.nock.core.util.Util;
import io.machinecode.nock.core.util.Util.ExpressionTransformer;
import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.Property;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collections;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PropertiesFactory implements ElementFactory<Properties, PropertiesImpl, Object> {

    public static final PropertiesFactory INSTANCE = new PropertiesFactory();

    private static final ExpressionTransformer<Property, PropertyImpl, JobPropertyContext> PROPERTY_BUILD_TRANSFORMER = new ExpressionTransformer<Property, PropertyImpl, JobPropertyContext>() {
        @Override
        public PropertyImpl transform(final Property that, final JobPropertyContext context) {
            return PropertyFactory.INSTANCE.produceDescriptor(that, context);
        }
    };

    @Override
    public PropertiesImpl produceDescriptor(final Properties that, final JobPropertyContext context) {
        final String partition;
        final List<PropertyImpl> properties;
        if (that == null) {
            partition = null;
            properties = Collections.emptyList();
        } else {
            partition = Expression.resolveDescriptorProperty(that.getPartition(), context);
            properties = Util.immutableCopy(that.getProperties(), context, PROPERTY_BUILD_TRANSFORMER);
        }
        return new PropertiesImpl(
                partition,
                properties
        );
    }

    @Override
    public Object produceExecution(final PropertiesImpl that, final JobParameterContext context) {
        throw new NotImplementedException();
    }

    @Override
    public Object producePartitioned(final Object that, final PartitionPropertyContext context) {
        throw new NotImplementedException();
    }
}
