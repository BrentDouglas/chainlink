package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyImpl;
import io.machinecode.nock.core.util.Util;
import io.machinecode.nock.core.util.Util.ExpressionTransformer;
import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.Property;

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
            return PropertyFactory.INSTANCE.produceBuildTime(that, context);
        }
    };

    private static final ExpressionTransformer<Property, PropertyImpl, PartitionPropertyContext> PROPERTY_PARTITION_TRANSFORMER = new ExpressionTransformer<Property, PropertyImpl, PartitionPropertyContext>() {
        @Override
        public PropertyImpl transform(final Property that, final PartitionPropertyContext context) {
            return PropertyFactory.INSTANCE.producePartitionTime(that, context);
        }
    };

    @Override
    public PropertiesImpl produceBuildTime(final Properties that, final JobPropertyContext context) {
        final String partition;
        final List<PropertyImpl> properties;
        if (that == null) {
            partition = null;
            properties = Collections.emptyList();
        } else {
            partition = Expression.resolveBuildTime(that.getPartition(), context);
            properties = Util.immutableCopy(that.getProperties(), context, PROPERTY_BUILD_TRANSFORMER);
        }
        return new PropertiesImpl(
                partition,
                properties
        );
    }

    @Override
    public PropertiesImpl producePartitionTime(final Properties that, final PartitionPropertyContext context) {
        final String partition;
        final List<PropertyImpl> properties;
        if (that == null) {
            partition = null;
            properties = Collections.emptyList();
        } else {
            partition = Expression.resolvePartition(that.getPartition(), context);
            properties = Util.immutableCopy(that.getProperties(), context, PROPERTY_PARTITION_TRANSFORMER);
        }
        return new PropertiesImpl(
                partition,
                properties
        );
    }
}
