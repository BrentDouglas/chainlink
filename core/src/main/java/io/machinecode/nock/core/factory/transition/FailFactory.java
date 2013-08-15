package io.machinecode.nock.core.factory.transition;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.model.transition.FailImpl;
import io.machinecode.nock.jsl.api.transition.Fail;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FailFactory implements ElementFactory<Fail, FailImpl> {

    public static final FailFactory INSTANCE = new FailFactory();

    @Override
    public FailImpl produceBuildTime(final Fail that, final JobPropertyContext context) {
        final String on = Expression.resolveBuildTime(that.getOn(), context);
        final String exitStatus = Expression.resolveBuildTime(that.getExitStatus(), context);
        return new FailImpl(on, exitStatus);
    }

    @Override
    public FailImpl producePartitionTime(final Fail that, final PartitionPropertyContext context) {
        final String on = Expression.resolvePartition(that.getOn(), context);
        final String exitStatus = Expression.resolvePartition(that.getExitStatus(), context);
        return new FailImpl(on, exitStatus);
    }
}
