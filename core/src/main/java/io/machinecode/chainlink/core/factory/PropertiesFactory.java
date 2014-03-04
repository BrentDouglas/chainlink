package io.machinecode.chainlink.core.factory;

import io.machinecode.chainlink.core.model.PropertiesImpl;
import io.machinecode.chainlink.core.model.PropertyImpl;
import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.util.Util;
import io.machinecode.chainlink.core.util.Util.ExpressionTransformer;
import io.machinecode.chainlink.spi.factory.ElementFactory;
import io.machinecode.chainlink.spi.element.Properties;
import io.machinecode.chainlink.spi.element.Property;
import io.machinecode.chainlink.spi.factory.JobPropertyContext;
import io.machinecode.chainlink.spi.factory.PropertyContext;

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
    private static final ExpressionTransformer<PropertyImpl, PropertyImpl, PropertyContext> PROPERTY_PARTITION_TRANSFORMER = new ExpressionTransformer<PropertyImpl, PropertyImpl, PropertyContext>() {
        @Override
        public PropertyImpl transform(final PropertyImpl that, final PropertyContext context) {
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
    public PropertiesImpl producePartitioned(final PropertiesImpl that, final PropertyContext context) {
        final String partition = Expression.resolvePartitionProperty(that.getPartition(), context);
        final List<PropertyImpl> properties = Util.immutableCopy(that.getProperties(), context, PROPERTY_PARTITION_TRANSFORMER);
        return new PropertiesImpl(
                partition,
                properties
        );
    }
}
