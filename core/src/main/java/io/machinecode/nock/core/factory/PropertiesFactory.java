package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.util.Util;
import io.machinecode.nock.core.util.Util.ExpressionTransformer;
import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.Property;

import java.util.Collections;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PropertiesFactory implements ElementFactory<Properties, PropertiesImpl> {

    public static final PropertiesFactory INSTANCE = new PropertiesFactory();

    private static final ExpressionTransformer<Property, PropertyImpl, JobPropertyContext> PROPERTY_BUILD_TRANSFORMER = new ExpressionTransformer<Property, PropertyImpl, JobPropertyContext>() {
        @Override
        public PropertyImpl transform(final Property that, final JobPropertyContext context) {
            return PropertyFactory.INSTANCE.produceExecution(that, context);
        }
    };
    private static final ExpressionTransformer<PropertyImpl, PropertyImpl, PartitionPropertyContext> PROPERTY_PARTITION_TRANSFORMER = new ExpressionTransformer<PropertyImpl, PropertyImpl, PartitionPropertyContext>() {
        @Override
        public PropertyImpl transform(final PropertyImpl that, final PartitionPropertyContext context) {
            return PropertyFactory.INSTANCE.producePartitioned(that, context);
        }
    };

    @Override
    public PropertiesImpl produceExecution(final Properties that, final JobPropertyContext context) {
        final String partition;
        final List<PropertyImpl> properties;
        if (that == null) {
            partition = null;
            properties = Collections.emptyList();
        } else {
            partition = Expression.resolveExecutionProperty(that.getPartition(), context);
            properties = Util.immutableCopy(that.getProperties(), context, PROPERTY_BUILD_TRANSFORMER);
        }
        return new PropertiesImpl(
                partition,
                properties
        );
    }

    @Override
    public PropertiesImpl producePartitioned(final PropertiesImpl that, final PartitionPropertyContext context) {
        final String partition = Expression.resolvePartitionProperty(that.getPartition(), context);
        final List<PropertyImpl> properties = Util.immutableCopy(that.getProperties(), context, PROPERTY_PARTITION_TRANSFORMER);
        return new PropertiesImpl(
                partition,
                properties
        );
    }
}
