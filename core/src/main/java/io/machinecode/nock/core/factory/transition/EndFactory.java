package io.machinecode.nock.core.factory.transition;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.descriptor.transition.EndImpl;
import io.machinecode.nock.core.work.transition.EndWork;
import io.machinecode.nock.spi.element.transition.End;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class EndFactory implements ElementFactory<End, EndImpl, EndWork> {

    public static final EndFactory INSTANCE = new EndFactory();

    @Override
    public EndImpl produceDescriptor(final End that, final JobPropertyContext context) {
        final String on = Expression.resolveDescriptorProperty(that.getOn(), context);
        final String exitStatus = Expression.resolveDescriptorProperty(that.getExitStatus(), context);
        return new EndImpl(on, exitStatus);
    }

    @Override
    public EndWork produceExecution(final EndImpl that, final JobParameterContext context) {
        final String on = Expression.resolveExecutionProperty(that.getOn(), context);
        final String exitStatus = Expression.resolveExecutionProperty(that.getExitStatus(), context);
        return new EndWork(on, exitStatus);
    }

    @Override
    public EndWork producePartitioned(final EndWork that, final PartitionPropertyContext context) {
        final String on = Expression.resolvePartitionProperty(that.getOn(), context);
        final String exitStatus = Expression.resolvePartitionProperty(that.getExitStatus(), context);
        return new EndWork(on, exitStatus);
    }
}
