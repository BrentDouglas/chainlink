package io.machinecode.nock.core.factory.execution;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.factory.ExecutionFactory;
import io.machinecode.nock.core.factory.transition.Transitions;
import io.machinecode.nock.core.model.execution.FlowImpl;
import io.machinecode.nock.core.model.transition.TransitionImpl;
import io.machinecode.nock.jsl.api.execution.Execution;
import io.machinecode.nock.jsl.api.execution.Flow;
import io.machinecode.nock.jsl.util.MutablePair;

import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FlowFactory implements ExecutionFactory<Flow, FlowImpl> {

    public static final FlowFactory INSTANCE = new FlowFactory();

    @Override
    public FlowImpl produceBuildTime(final Flow that, final Execution execution, final JobPropertyContext context) {
        final List<MutablePair<String,String>> jobProperties = context.getProperties();
        final String id = Expression.resolveBuildTime(that.getId(), jobProperties);
        final String next = Expression.resolveBuildTime(that.getNext() == null ? execution == null ? null : execution.getId() : that.getNext(), jobProperties);
        final List<Execution> executions = Executions.immutableCopyExecutionsBuildTime(that.getExecutions(), context);
        final List<TransitionImpl> transitions = Transitions.immutableCopyTransitionsBuildTime(that.getTransitions(), context);
        return new FlowImpl(
                id,
                next,
                executions,
                transitions
        );
    }

    @Override
    public FlowImpl produceStartTime(final Flow that, final Properties parameters) {
        final String id = Expression.resolveStartTime(that.getId(), parameters);
        final String next = Expression.resolveStartTime(that.getNext(), parameters);
        final List<Execution> executions = Executions.immutableCopyExecutionsStartTime(that.getExecutions(), parameters);
        final List<TransitionImpl> transitions = Transitions.immutableCopyTransitionsStartTime(that.getTransitions(), parameters);
        return new FlowImpl(
                id,
                next,
                executions,
                transitions
        );
    }

    @Override
    public FlowImpl producePartitionTime(final Flow that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> partitionPlan = context.getProperties();
        final String id = Expression.resolvePartition(that.getId(), partitionPlan);
        final String next = Expression.resolvePartition(that.getNext(), partitionPlan);
        final List<Execution> executions = Executions.immutableCopyExecutionsPartitionTime(that.getExecutions(), context);
        final List<TransitionImpl> transitions = Transitions.immutableCopyTransitionsPartitionTime(that.getTransitions(), context);
        return new FlowImpl(
                id,
                next,
                executions,
                transitions
        );
    }
}
