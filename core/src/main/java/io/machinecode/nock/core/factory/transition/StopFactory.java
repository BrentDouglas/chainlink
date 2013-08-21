package io.machinecode.nock.core.factory.transition;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.descriptor.transition.StopImpl;
import io.machinecode.nock.core.work.transition.StopWork;
import io.machinecode.nock.spi.element.transition.Stop;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StopFactory implements ElementFactory<Stop, StopImpl, StopWork> {

    public static final StopFactory INSTANCE = new StopFactory();

    @Override
    public StopImpl produceDescriptor(final Stop that, final JobPropertyContext context) {
        final String on = Expression.resolveDescriptorProperty(that.getOn(), context);
        final String exitStatus = Expression.resolveDescriptorProperty(that.getExitStatus(), context);
        final String restart = Expression.resolveDescriptorProperty(that.getRestart(), context);
        return new StopImpl(on, exitStatus, restart);
    }

    @Override
    public StopWork produceExecution(final StopImpl that, final JobParameterContext context) {
        final String on = Expression.resolveExecutionProperty(that.getOn(), context);
        final String restart = Expression.resolveExecutionProperty(that.getRestart(), context);
        final String exitStatus = Expression.resolveExecutionProperty(that.getExitStatus(), context);
        return new StopWork(on, restart, exitStatus);
    }

    @Override
    public StopWork producePartitioned(final StopWork that, final PartitionPropertyContext context) {
        final String on = Expression.resolvePartitionProperty(that.getOn(), context);
        final String exitStatus = Expression.resolvePartitionProperty(that.getExitStatus(), context);
        final String restart = Expression.resolvePartitionProperty(that.getRestart(), context);
        return new StopWork(on, exitStatus, restart);
    }
}
