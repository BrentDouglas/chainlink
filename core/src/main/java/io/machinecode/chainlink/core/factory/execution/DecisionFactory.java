package io.machinecode.chainlink.core.factory.execution;

import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.core.expression.PartitionPropertyContext;
import io.machinecode.chainlink.core.factory.PropertiesFactory;
import io.machinecode.chainlink.core.factory.transition.Transitions;
import io.machinecode.chainlink.core.inject.ArtifactReferenceImpl;
import io.machinecode.chainlink.core.jsl.impl.PropertiesImpl;
import io.machinecode.chainlink.core.jsl.impl.execution.DecisionImpl;
import io.machinecode.chainlink.core.jsl.impl.transition.TransitionImpl;
import io.machinecode.chainlink.spi.jsl.execution.Decision;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class DecisionFactory {

    public static DecisionImpl produceExecution(final Decision that, final JobPropertyContext context) {
        final String id = Expression.resolveExecutionProperty(that.getId(), context);
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.produceExecution(that.getProperties(), context);
        final List<TransitionImpl> transitions = Transitions.immutableCopyTransitionsDescriptor(that.getTransitions(), context);
        return new DecisionImpl(
                id,
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties,
                transitions
        );
    }

    public static DecisionImpl producePartitioned(final DecisionImpl that, final PartitionPropertyContext context) {
        final String id = Expression.resolvePartitionProperty(that.getId(), context);
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.producePartitioned(that.getProperties(), context);
        final List<TransitionImpl> transitions = Transitions.immutableCopyTransitionsPartition(that.getTransitions(), context);
        return new DecisionImpl(
                id,
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties,
                transitions
        );
    }
}
