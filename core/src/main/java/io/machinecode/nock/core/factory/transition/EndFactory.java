package io.machinecode.nock.core.factory.transition;

import io.machinecode.nock.core.model.transition.EndImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.spi.factory.ElementFactory;
import io.machinecode.nock.spi.element.transition.End;
import io.machinecode.nock.spi.factory.JobPropertyContext;
import io.machinecode.nock.spi.factory.PropertyContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class EndFactory implements ElementFactory<End, EndImpl> {

    public static final EndFactory INSTANCE = new EndFactory();

    @Override
    public EndImpl produceExecution(final End that, final JobPropertyContext context) {
        final String on = Expression.resolveExecutionProperty(that.getOn(), context);
        final String exitStatus = Expression.resolveExecutionProperty(that.getExitStatus(), context);
        return new EndImpl(on, exitStatus);
    }

    @Override
    public EndImpl producePartitioned(final EndImpl that, final PropertyContext context) {
        final String on = Expression.resolvePartitionProperty(that.getOn(), context);
        final String exitStatus = Expression.resolvePartitionProperty(that.getExitStatus(), context);
        return new EndImpl(on, exitStatus);
    }
}
