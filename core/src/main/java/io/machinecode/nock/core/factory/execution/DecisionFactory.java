package io.machinecode.nock.core.factory.execution;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.factory.transition.Transitions;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.execution.DecisionImpl;
import io.machinecode.nock.core.model.transition.TransitionImpl;
import io.machinecode.nock.jsl.api.execution.Decision;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class DecisionFactory implements ElementFactory<Decision, DecisionImpl> {

    public static final DecisionFactory INSTANCE = new DecisionFactory();

    @Override
    public DecisionImpl produceBuildTime(final Decision that, final JobPropertyContext context) {
        final String id = Expression.resolveBuildTime(that.getId(), context);
        final String ref = Expression.resolveBuildTime(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceBuildTime(that.getProperties(), context);
        final List<TransitionImpl> transitions = Transitions.immutableCopyTransitionsBuildTime(that.getTransitions(), context);
        return new DecisionImpl(
                id,
                ref,
                properties,
                transitions
        );
    }

    @Override
    public DecisionImpl producePartitionTime(final Decision that, final PartitionPropertyContext context) {
        final String id = Expression.resolvePartition(that.getId(), context);
        final String ref = Expression.resolvePartition(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitionTime(that.getProperties(), context);
        final List<TransitionImpl> transitions = Transitions.immutableCopyTransitionsPartitionTime(that.getTransitions(), context);
        return new DecisionImpl(
                id,
                ref,
                properties,
                transitions
        );
    }
}
