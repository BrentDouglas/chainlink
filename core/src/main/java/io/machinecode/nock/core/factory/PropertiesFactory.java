package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyImpl;
import io.machinecode.nock.core.util.Util;
import io.machinecode.nock.core.util.Util.ExpressionTransformer;
import io.machinecode.nock.core.util.Util.ParametersTransformer;
import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.Property;
import io.machinecode.nock.jsl.util.MutablePair;

import java.util.Collections;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PropertiesFactory implements ElementFactory<Properties, PropertiesImpl> {

    public static final PropertiesFactory INSTANCE = new PropertiesFactory();

    private static final ExpressionTransformer<Property, PropertyImpl> PROPERTY_BUILD_TRANSFORMER = new ExpressionTransformer<Property, PropertyImpl>() {
        @Override
        public PropertyImpl transform(final Property that, final JobPropertyContext context) {
            return PropertyFactory.INSTANCE.produceBuildTime(that, context);
        }
    };

    private static final ParametersTransformer<Property, PropertyImpl> PROPERTY_START_TRANSFORMER = new ParametersTransformer<Property, PropertyImpl>() {
        @Override
        public PropertyImpl transform(final Property that, final java.util.Properties parameters) {
            return PropertyFactory.INSTANCE.produceStartTime(that, parameters);
        }
    };

    private static final ExpressionTransformer<Property, PropertyImpl> PROPERTY_PARTITION_TRANSFORMER = new ExpressionTransformer<Property, PropertyImpl>() {
        @Override
        public PropertyImpl transform(final Property that, final JobPropertyContext context) {
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
            final List<MutablePair<String,String>> jobProperties = context.getProperties();
            partition = Expression.resolveBuildTime(that.getPartition(), jobProperties);
            properties = Util.immutableCopy(that.getProperties(), context, PROPERTY_BUILD_TRANSFORMER);
        }
        return new PropertiesImpl(
                partition,
                properties
        );
    }

    @Override
    public PropertiesImpl produceStartTime(final Properties that, final java.util.Properties parameters) {
        final String partition = Expression.resolveStartTime(that.getPartition(), parameters);
        final List<PropertyImpl> properties = Util.immutableCopy(that.getProperties(), parameters, PROPERTY_START_TRANSFORMER);
        return new PropertiesImpl(
                partition,
                properties
        );
    }

    @Override
    public PropertiesImpl producePartitionTime(final Properties that, final JobPropertyContext context) {
        final String partition;
        final List<PropertyImpl> properties;
        if (that == null) {
            partition = null;
            properties = Collections.emptyList();
        } else {
            final List<MutablePair<String,String>> partitionPlan = context.getProperties();
            partition = Expression.resolvePartition(that.getPartition(), partitionPlan);
            properties = Util.immutableCopy(that.getProperties(), context, PROPERTY_PARTITION_TRANSFORMER);
        }
        return new PropertiesImpl(
                partition,
                properties
        );
    }
}
