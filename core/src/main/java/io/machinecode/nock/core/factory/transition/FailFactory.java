package io.machinecode.nock.core.factory.transition;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.descriptor.transition.FailImpl;
import io.machinecode.nock.core.work.transition.FailWork;
import io.machinecode.nock.spi.element.transition.Fail;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FailFactory implements ElementFactory<Fail, FailImpl, FailWork> {

    public static final FailFactory INSTANCE = new FailFactory();

    @Override
    public FailImpl produceDescriptor(final Fail that, final JobPropertyContext context) {
        final String on = Expression.resolveDescriptorProperty(that.getOn(), context);
        final String exitStatus = Expression.resolveDescriptorProperty(that.getExitStatus(), context);
        return new FailImpl(on, exitStatus);
    }

    @Override
    public FailWork produceExecution(final FailImpl that, final JobParameterContext context) {
        final String on = Expression.resolveExecutionProperty(that.getOn(), context);
        final String exitStatus = Expression.resolveExecutionProperty(that.getExitStatus(), context);
        return new FailWork(on, exitStatus);
    }

    @Override
    public FailWork producePartitioned(final FailWork that, final PartitionPropertyContext context) {
        final String on = Expression.resolvePartitionProperty(that.getOn(), context);
        final String exitStatus = Expression.resolvePartitionProperty(that.getExitStatus(), context);
        return new FailWork(on, exitStatus);
    }
}
