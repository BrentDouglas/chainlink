package io.machinecode.chainlink.core.factory.transition;

import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.core.expression.PartitionPropertyContext;
import io.machinecode.chainlink.core.jsl.impl.transition.FailImpl;
import io.machinecode.chainlink.spi.jsl.transition.Fail;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FailFactory {

    public static FailImpl produceExecution(final Fail that, final JobPropertyContext context) {
        final String on = Expression.resolveExecutionProperty(that.getOn(), context);
        final String exitStatus = Expression.resolveExecutionProperty(that.getExitStatus(), context);
        return new FailImpl(on, exitStatus);
    }

    public static FailImpl producePartitioned(final FailImpl that, final PartitionPropertyContext context) {
        final String on = Expression.resolvePartitionProperty(that.getOn(), context);
        final String exitStatus = Expression.resolvePartitionProperty(that.getExitStatus(), context);
        return new FailImpl(on, exitStatus);
    }
}
