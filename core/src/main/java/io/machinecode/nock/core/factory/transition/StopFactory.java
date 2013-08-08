package io.machinecode.nock.core.factory.transition;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.model.transition.StopImpl;
import io.machinecode.nock.jsl.api.transition.Stop;
import io.machinecode.nock.jsl.util.MutablePair;

import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StopFactory implements ElementFactory<Stop, StopImpl> {

    public static final StopFactory INSTANCE = new StopFactory();

    @Override
    public StopImpl produceBuildTime(final Stop that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> jobProperties = context.getProperties();
        final String on = Expression.resolveBuildTime(that.getOn(), jobProperties);
        final String exitStatus = Expression.resolveBuildTime(that.getExitStatus(), jobProperties);
        final String restart = Expression.resolveBuildTime(that.getRestart(), jobProperties);
        return new StopImpl(on, exitStatus, restart);
    }

    @Override
    public StopImpl produceStartTime(final Stop that, final Properties parameters) {
        final String on = Expression.resolveStartTime(that.getOn(), parameters);
        final String exitStatus = Expression.resolveStartTime(that.getExitStatus(), parameters);
        final String restart = Expression.resolveStartTime(that.getRestart(), parameters);
        return new StopImpl(on, exitStatus, restart);
    }

    @Override
    public StopImpl producePartitionTime(final Stop that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> partitionPlan = context.getProperties();
        final String on = Expression.resolvePartition(that.getOn(), partitionPlan);
        final String exitStatus = Expression.resolvePartition(that.getExitStatus(), partitionPlan);
        final String restart = Expression.resolvePartition(that.getRestart(), partitionPlan);
        return new StopImpl(on, exitStatus, restart);
    }
}
