package io.machinecode.nock.core.factory.transition;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.model.transition.NextImpl;
import io.machinecode.nock.jsl.api.transition.Next;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class NextFactory implements ElementFactory<Next, NextImpl> {

    public static final NextFactory INSTANCE = new NextFactory();

    @Override
    public NextImpl produceBuildTime(final Next that, final JobPropertyContext context) {
        final String on = Expression.resolveBuildTime(that.getOn(), context);
        final String to = Expression.resolveBuildTime(that.getTo(), context);
        return new NextImpl(on, to);
    }

    @Override
    public NextImpl producePartitionTime(final Next that, final PartitionPropertyContext context) {
        final String on = Expression.resolvePartition(that.getOn(), context);
        final String to = Expression.resolvePartition(that.getTo(), context);
        return new NextImpl(on, to);
    }
}
