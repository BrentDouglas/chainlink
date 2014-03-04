package io.machinecode.chainlink.core.factory.execution;

import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.factory.PropertiesFactory;
import io.machinecode.chainlink.core.factory.transition.Transitions;
import io.machinecode.chainlink.core.loader.ArtifactReferenceImpl;
import io.machinecode.chainlink.core.model.PropertiesImpl;
import io.machinecode.chainlink.core.model.execution.DecisionImpl;
import io.machinecode.chainlink.core.model.transition.TransitionImpl;
import io.machinecode.chainlink.spi.element.execution.Decision;
import io.machinecode.chainlink.spi.factory.ElementFactory;
import io.machinecode.chainlink.spi.factory.JobPropertyContext;
import io.machinecode.chainlink.spi.factory.PropertyContext;

import javax.batch.api.Decider;
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
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties,
                transitions
        );
    }

    @Override
    public DecisionImpl producePartitioned(final DecisionImpl that, final PropertyContext context) {
        final String id = Expression.resolvePartitionProperty(that.getId(), context);
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitioned(that.getProperties(), context);
        final List<TransitionImpl> transitions = Transitions.immutableCopyTransitionsPartition(that.getTransitions(), context);
        return new DecisionImpl(
                id,
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties,
                transitions
        );
    }
}
