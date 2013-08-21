package io.machinecode.nock.core.factory.execution;

import io.machinecode.nock.core.descriptor.PropertiesImpl;
import io.machinecode.nock.core.descriptor.execution.DecisionImpl;
import io.machinecode.nock.core.descriptor.transition.TransitionImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.factory.transition.Transitions;
import io.machinecode.nock.core.work.execution.DecisionWork;
import io.machinecode.nock.core.work.transition.TransitionWork;
import io.machinecode.nock.spi.element.execution.Decision;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class DecisionFactory implements ElementFactory<Decision, DecisionImpl, DecisionWork> {

    public static final DecisionFactory INSTANCE = new DecisionFactory();

    @Override
    public DecisionImpl produceDescriptor(final Decision that, final JobPropertyContext context) {
        final String id = Expression.resolveDescriptorProperty(that.getId(), context);
        final String ref = Expression.resolveDescriptorProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceDescriptor(that.getProperties(), context);
        final List<TransitionImpl> transitions = Transitions.immutableCopyTransitionsDescriptor(that.getTransitions(), context);
        return new DecisionImpl(
                id,
                ref,
                properties,
                transitions
        );
    }

    @Override
    public DecisionWork produceExecution(final DecisionImpl that, final JobParameterContext context) {
        final String id = Expression.resolveExecutionProperty(that.getId(), context);
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        final List<TransitionWork> transitions = Transitions.immutableCopyTransitionsExecution(that.getTransitions(), context);
        return new DecisionWork(
                id,
                ref,
                transitions
        );
    }

    @Override
    public DecisionWork producePartitioned(final DecisionWork that, final PartitionPropertyContext context) {
        final String id = Expression.resolvePartitionProperty(that.getId(), context);
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        final List<TransitionWork> transitions = Transitions.immutableCopyTransitionsPartition(that.getTransitions(), context);
        return new DecisionWork(
                id,
                ref,
                transitions
        );
    }
}
