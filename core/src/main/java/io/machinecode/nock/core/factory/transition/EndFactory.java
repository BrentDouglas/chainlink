package io.machinecode.nock.core.factory.transition;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.model.transition.EndImpl;
import io.machinecode.nock.jsl.api.transition.End;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class EndFactory implements ElementFactory<End, EndImpl> {

    public static final EndFactory INSTANCE = new EndFactory();

    @Override
    public EndImpl produceBuildTime(final End that, final JobPropertyContext context) {
        final String on = Expression.resolveBuildTime(that.getOn(), context);
        final String exitStatus = Expression.resolveBuildTime(that.getExitStatus(), context);
        return new EndImpl(on, exitStatus);
    }

    @Override
    public EndImpl producePartitionTime(final End that, final PartitionPropertyContext context) {
        final String on = Expression.resolvePartition(that.getOn(), context);
        final String exitStatus = Expression.resolvePartition(that.getExitStatus(), context);
        return new EndImpl(on, exitStatus);
    }
}
