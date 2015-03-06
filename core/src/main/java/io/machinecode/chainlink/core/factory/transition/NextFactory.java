package io.machinecode.chainlink.core.factory.transition;

import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.core.expression.PartitionPropertyContext;
import io.machinecode.chainlink.core.jsl.impl.transition.NextImpl;
import io.machinecode.chainlink.spi.jsl.transition.Next;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class NextFactory {

    public static NextImpl produceExecution(final Next that, final JobPropertyContext context) {
        final String on = Expression.resolveExecutionProperty(that.getOn(), context);
        final String to = Expression.resolveExecutionProperty(that.getTo(), context);
        return new NextImpl(on, to);
    }

    public static NextImpl producePartitioned(final NextImpl that, final PartitionPropertyContext context) {
        final String on = Expression.resolvePartitionProperty(that.getOn(), context);
        final String to = Expression.resolvePartitionProperty(that.getTo(), context);
        return new NextImpl(on, to);
    }
}
