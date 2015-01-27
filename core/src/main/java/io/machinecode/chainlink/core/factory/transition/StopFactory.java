package io.machinecode.chainlink.core.factory.transition;

import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.core.factory.ElementFactory;
import io.machinecode.chainlink.core.jsl.impl.transition.StopImpl;
import io.machinecode.chainlink.spi.expression.PropertyContext;
import io.machinecode.chainlink.spi.jsl.transition.Stop;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
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
