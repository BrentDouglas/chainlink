package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.model.PropertyImpl;
import io.machinecode.nock.jsl.api.Property;
import io.machinecode.nock.jsl.util.MutablePair;

import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PropertyFactory implements ElementFactory<Property, PropertyImpl> {

    public static final PropertyFactory INSTANCE = new PropertyFactory();

    @Override
    public PropertyImpl produceBuildTime(final Property that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> jobProperties = context.getProperties();
        final String name = Expression.resolveBuildTime(that.getName(), jobProperties);
        final String value = Expression.resolveBuildTime(that.getValue(), jobProperties);
        return new PropertyImpl(name, value);
    }

    @Override
    public PropertyImpl produceStartTime(final Property that, final Properties parameters) {
        final String name = Expression.resolveStartTime(that.getName(), parameters);
        final String value = Expression.resolveStartTime(that.getValue(), parameters);
        return new PropertyImpl(name, value);
    }

    @Override
    public PropertyImpl producePartitionTime(final Property that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> partitionPlan = context.getProperties();
        final String name = Expression.resolvePartition(that.getName(), partitionPlan);
        final String value = Expression.resolvePartition(that.getValue(), partitionPlan);
        return new PropertyImpl(name, value);
    }
}
