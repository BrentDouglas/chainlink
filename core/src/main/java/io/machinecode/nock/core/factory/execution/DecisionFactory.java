package io.machinecode.nock.core.factory.execution;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.factory.transition.Transitions;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.execution.DecisionImpl;
import io.machinecode.nock.core.model.transition.TransitionImpl;
import io.machinecode.nock.jsl.api.execution.Decision;
import io.machinecode.nock.jsl.util.MutablePair;

import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class DecisionFactory implements ElementFactory<Decision, DecisionImpl> {

    public static final DecisionFactory INSTANCE = new DecisionFactory();

    @Override
    public DecisionImpl produceBuildTime(final Decision that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> jobProperties = context.getProperties();
        final String id = Expression.resolveBuildTime(that.getId(), jobProperties);
        final String ref = Expression.resolveBuildTime(that.getRef(), jobProperties);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceBuildTime(that.getProperties(), context);
        context.addProperties(properties);
        final List<TransitionImpl> transitions = Transitions.immutableCopyTransitionsBuildTime(that.getTransitions(), context);
        return new DecisionImpl(
                id,
                ref,
                properties,
                transitions
        );
    }

    @Override
    public DecisionImpl produceStartTime(final Decision that, final Properties parameters) {
        final String id = Expression.resolveStartTime(that.getId(), parameters);
        final String ref = Expression.resolveStartTime(that.getRef(), parameters);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceStartTime(that.getProperties(), parameters);
        final List<TransitionImpl> transitions = Transitions.immutableCopyTransitionsStartTime(that.getTransitions(), parameters);
        return new DecisionImpl(
                id,
                ref,
                properties,
                transitions
        );
    }

    @Override
    public DecisionImpl producePartitionTime(final Decision that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> partitionPlan = context.getProperties();
        final String id = Expression.resolvePartition(that.getId(), partitionPlan);
        final String ref = Expression.resolvePartition(that.getRef(), partitionPlan);
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
