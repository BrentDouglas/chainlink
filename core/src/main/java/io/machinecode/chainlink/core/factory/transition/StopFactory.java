package io.machinecode.chainlink.core.factory.transition;

import io.machinecode.chainlink.core.element.transition.StopImpl;
import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.factory.ElementFactory;
import io.machinecode.chainlink.spi.element.transition.Stop;
import io.machinecode.chainlink.spi.expression.JobPropertyContext;
import io.machinecode.chainlink.spi.expression.PropertyContext;

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
