package io.machinecode.nock.core.factory.transition;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.model.transition.FailImpl;
import io.machinecode.nock.jsl.api.transition.Fail;
import io.machinecode.nock.jsl.util.MutablePair;

import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FailFactory implements ElementFactory<Fail, FailImpl> {

    public static final FailFactory INSTANCE = new FailFactory();

    @Override
    public FailImpl produceBuildTime(final Fail that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> jobProperties = context.getProperties();
        final String on = Expression.resolveBuildTime(that.getOn(), jobProperties);
        final String exitStatus = Expression.resolveBuildTime(that.getExitStatus(), jobProperties);
        return new FailImpl(on, exitStatus);
    }

    @Override
    public FailImpl produceStartTime(final Fail that, final Properties parameters) {
        final String on = Expression.resolveStartTime(that.getOn(), parameters);
        final String exitStatus = Expression.resolveStartTime(that.getExitStatus(), parameters);
        return new FailImpl(on, exitStatus);
    }

    @Override
    public FailImpl producePartitionTime(final Fail that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> partitionPlan = context.getProperties();
        final String on = Expression.resolvePartition(that.getOn(), partitionPlan);
        final String exitStatus = Expression.resolvePartition(that.getExitStatus(), partitionPlan);
        return new FailImpl(on, exitStatus);
    }
}
