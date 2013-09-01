package io.machinecode.nock.core.factory.transition;

import io.machinecode.nock.core.model.transition.FailImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.spi.factory.ElementFactory;
import io.machinecode.nock.spi.element.transition.Fail;
import io.machinecode.nock.spi.factory.JobPropertyContext;
import io.machinecode.nock.spi.factory.PropertyContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
