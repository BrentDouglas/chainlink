package io.machinecode.nock.core.factory.transition;

import io.machinecode.nock.core.model.transition.NextImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.spi.factory.ElementFactory;
import io.machinecode.nock.spi.element.transition.Next;
import io.machinecode.nock.spi.factory.JobPropertyContext;
import io.machinecode.nock.spi.factory.PropertyContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class NextFactory implements ElementFactory<Next, NextImpl> {

    public static final NextFactory INSTANCE = new NextFactory();

    @Override
    public NextImpl produceExecution(final Next that, final JobPropertyContext context) {
        final String on = Expression.resolveExecutionProperty(that.getOn(), context);
        final String to = Expression.resolveExecutionProperty(that.getTo(), context);
        return new NextImpl(on, to);
    }

    @Override
    public NextImpl producePartitioned(final NextImpl that, final PropertyContext context) {
        final String on = Expression.resolvePartitionProperty(that.getOn(), context);
        final String to = Expression.resolvePartitionProperty(that.getTo(), context);
        return new NextImpl(on, to);
    }
}
