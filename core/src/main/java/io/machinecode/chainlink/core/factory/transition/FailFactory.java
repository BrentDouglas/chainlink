package io.machinecode.chainlink.core.factory.transition;

import io.machinecode.chainlink.core.element.transition.FailImpl;
import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.factory.ElementFactory;
import io.machinecode.chainlink.spi.element.transition.Fail;
import io.machinecode.chainlink.spi.expression.JobPropertyContext;
import io.machinecode.chainlink.spi.expression.PropertyContext;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class FailFactory implements ElementFactory<Fail, FailImpl> {

    public static final FailFactory INSTANCE = new FailFactory();

    @Override
    public FailImpl produceExecution(final Fail that, final JobPropertyContext context) {
        final String on = Expression.resolveExecutionProperty(that.getOn(), context);
        final String exitStatus = Expression.resolveExecutionProperty(that.getExitStatus(), context);
        return new FailImpl(on, exitStatus);
    }

    @Override
    public FailImpl producePartitioned(final FailImpl that, final PropertyContext context) {
        final String on = Expression.resolvePartitionProperty(that.getOn(), context);
        final String exitStatus = Expression.resolvePartitionProperty(that.getExitStatus(), context);
        return new FailImpl(on, exitStatus);
    }
}
