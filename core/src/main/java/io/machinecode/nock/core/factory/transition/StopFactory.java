package io.machinecode.nock.core.factory.transition;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.model.transition.StopImpl;
import io.machinecode.nock.jsl.api.transition.Stop;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StopFactory implements ElementFactory<Stop, StopImpl> {

    public static final StopFactory INSTANCE = new StopFactory();

    @Override
    public StopImpl produceBuildTime(final Stop that, final JobPropertyContext context) {
        final String on = Expression.resolveBuildTime(that.getOn(), context);
        final String exitStatus = Expression.resolveBuildTime(that.getExitStatus(), context);
        final String restart = Expression.resolveBuildTime(that.getRestart(), context);
        return new StopImpl(on, exitStatus, restart);
    }

    @Override
    public StopImpl producePartitionTime(final Stop that, final PartitionPropertyContext context) {
        final String on = Expression.resolvePartition(that.getOn(), context);
        final String exitStatus = Expression.resolvePartition(that.getExitStatus(), context);
        final String restart = Expression.resolvePartition(that.getRestart(), context);
        return new StopImpl(on, exitStatus, restart);
    }
}
