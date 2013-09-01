package io.machinecode.nock.core.factory.transition;

import io.machinecode.nock.core.model.transition.StopImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.spi.factory.ElementFactory;
import io.machinecode.nock.spi.element.transition.Stop;
import io.machinecode.nock.spi.factory.JobPropertyContext;
import io.machinecode.nock.spi.factory.PropertyContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StopFactory implements ElementFactory<Stop, StopImpl> {

    public static final StopFactory INSTANCE = new StopFactory();

    @Override
    public StopImpl produceExecution(final Stop that, final JobPropertyContext context) {
        final String on = Expression.resolveExecutionProperty(that.getOn(), context);
        final String exitStatus = Expression.resolveExecutionProperty(that.getExitStatus(), context);
        final String restart = Expression.resolveExecutionProperty(that.getRestart(), context);
        return new StopImpl(on, exitStatus, restart);
    }

    @Override
    public StopImpl producePartitioned(final StopImpl that, final PropertyContext context) {
        final String on = Expression.resolvePartitionProperty(that.getOn(), context);
        final String exitStatus = Expression.resolvePartitionProperty(that.getExitStatus(), context);
        final String restart = Expression.resolvePartitionProperty(that.getRestart(), context);
        return new StopImpl(on, exitStatus, restart);
    }
}
