package io.machinecode.nock.core.factory.transition;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.model.transition.EndImpl;
import io.machinecode.nock.jsl.api.transition.End;
import io.machinecode.nock.jsl.util.MutablePair;

import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class EndFactory implements ElementFactory<End, EndImpl> {

    public static final EndFactory INSTANCE = new EndFactory();

    @Override
    public EndImpl produceBuildTime(final End that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> jobProperties = context.getProperties();
        final String on = Expression.resolveBuildTime(that.getOn(), jobProperties);
        final String exitStatus = Expression.resolveBuildTime(that.getExitStatus(), jobProperties);
        return new EndImpl(on, exitStatus);
    }

    @Override
    public EndImpl produceStartTime(final End that, final Properties parameters) {
        final String on = Expression.resolveStartTime(that.getOn(), parameters);
        final String exitStatus = Expression.resolveStartTime(that.getExitStatus(), parameters);
        return new EndImpl(on, exitStatus);
    }

    @Override
    public EndImpl producePartitionTime(final End that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> partitionPlan = context.getProperties();
        final String on = Expression.resolvePartition(that.getOn(), partitionPlan);
        final String exitStatus = Expression.resolvePartition(that.getExitStatus(), partitionPlan);
        return new EndImpl(on, exitStatus);
    }
}
