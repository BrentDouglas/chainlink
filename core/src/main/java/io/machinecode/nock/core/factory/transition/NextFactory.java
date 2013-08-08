package io.machinecode.nock.core.factory.transition;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.model.transition.NextImpl;
import io.machinecode.nock.jsl.api.transition.Next;
import io.machinecode.nock.jsl.util.MutablePair;

import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class NextFactory implements ElementFactory<Next, NextImpl> {

    public static final NextFactory INSTANCE = new NextFactory();

    @Override
    public NextImpl produceBuildTime(final Next that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> jobProperties = context.getProperties();
        final String on = Expression.resolveBuildTime(that.getOn(), jobProperties);
        final String to = Expression.resolveBuildTime(that.getTo(), jobProperties);
        return new NextImpl(on, to);
    }

    @Override
    public NextImpl produceStartTime(final Next that, final Properties parameters) {
        final String on = Expression.resolveStartTime(that.getOn(), parameters);
        final String to = Expression.resolveStartTime(that.getTo(), parameters);
        return new NextImpl(on, to);
    }

    @Override
    public NextImpl producePartitionTime(final Next that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> partitionPlan = context.getProperties();
        final String on = Expression.resolvePartition(that.getOn(), partitionPlan);
        final String to = Expression.resolvePartition(that.getTo(), partitionPlan);
        return new NextImpl(on, to);
    }
}
