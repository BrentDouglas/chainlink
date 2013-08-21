package io.machinecode.nock.core.factory.transition;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.descriptor.transition.NextImpl;
import io.machinecode.nock.core.work.transition.NextWork;
import io.machinecode.nock.spi.element.transition.Next;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class NextFactory implements ElementFactory<Next, NextImpl, NextWork> {

    public static final NextFactory INSTANCE = new NextFactory();

    @Override
    public NextImpl produceDescriptor(final Next that, final JobPropertyContext context) {
        final String on = Expression.resolveDescriptorProperty(that.getOn(), context);
        final String to = Expression.resolveDescriptorProperty(that.getTo(), context);
        return new NextImpl(on, to);
    }

    @Override
    public NextWork produceExecution(final NextImpl that, final JobParameterContext context) {
        final String on = Expression.resolveExecutionProperty(that.getOn(), context);
        final String to = Expression.resolveExecutionProperty(that.getTo(), context);
        return new NextWork(on, to);
    }

    @Override
    public NextWork producePartitioned(final NextWork that, final PartitionPropertyContext context) {
        final String on = Expression.resolvePartitionProperty(that.getOn(), context);
        final String to = Expression.resolvePartitionProperty(that.getTo(), context);
        return new NextWork(on, to);
    }
}
