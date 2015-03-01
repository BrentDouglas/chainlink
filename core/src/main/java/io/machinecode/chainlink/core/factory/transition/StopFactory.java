package io.machinecode.chainlink.core.factory.transition;

import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.core.expression.PropertyContext;
import io.machinecode.chainlink.core.jsl.impl.transition.StopImpl;
import io.machinecode.chainlink.spi.jsl.transition.Stop;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class StopFactory {

    public static StopImpl produceExecution(final Stop that, final JobPropertyContext context) {
        final String on = Expression.resolveExecutionProperty(that.getOn(), context);
        final String exitStatus = Expression.resolveExecutionProperty(that.getExitStatus(), context);
        final String restart = Expression.resolveExecutionProperty(that.getRestart(), context);
        return new StopImpl(on, exitStatus, restart);
    }

    public static StopImpl producePartitioned(final StopImpl that, final PropertyContext context) {
        final String on = Expression.resolvePartitionProperty(that.getOn(), context);
        final String exitStatus = Expression.resolvePartitionProperty(that.getExitStatus(), context);
        final String restart = Expression.resolvePartitionProperty(that.getRestart(), context);
        return new StopImpl(on, exitStatus, restart);
    }
}
