package io.machinecode.nock.core.factory.execution;

import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.execution.DecisionImpl;
import io.machinecode.nock.core.model.transition.TransitionImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.factory.transition.Transitions;
import io.machinecode.nock.spi.element.execution.Decision;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class DecisionFactory implements ElementFactory<Decision, DecisionImpl> {

    public static final DecisionFactory INSTANCE = new DecisionFactory();

    @Override
    public DecisionImpl produceExecution(final Decision that, final JobPropertyContext context) {
        final String id = Expression.resolveExecutionProperty(that.getId(), context);
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceExecution(that.getProperties(), context);
        final List<TransitionImpl> transitions = Transitions.immutableCopyTransitionsDescriptor(that.getTransitions(), context);
        return new DecisionImpl(
                id,
                ref,
                properties,
                transitions
        );
    }

    @Override
    public DecisionImpl producePartitioned(final DecisionImpl that, final PartitionPropertyContext context) {
        final String id = Expression.resolvePartitionProperty(that.getId(), context);
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitioned(that.getProperties(), context);
        final List<TransitionImpl> transitions = Transitions.immutableCopyTransitionsPartition(that.getTransitions(), context);
        return new DecisionImpl(
                id,
                ref,
                properties,
                transitions
        );
    }
}
