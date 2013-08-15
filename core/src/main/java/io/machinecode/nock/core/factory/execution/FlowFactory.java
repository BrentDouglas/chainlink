package io.machinecode.nock.core.factory.execution;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ExecutionFactory;
import io.machinecode.nock.core.factory.transition.Transitions;
import io.machinecode.nock.core.model.execution.FlowImpl;
import io.machinecode.nock.core.model.transition.TransitionImpl;
import io.machinecode.nock.jsl.api.execution.Execution;
import io.machinecode.nock.jsl.api.execution.Flow;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FlowFactory implements ExecutionFactory<Flow, FlowImpl> {

    public static final FlowFactory INSTANCE = new FlowFactory();

    @Override
    public FlowImpl produceBuildTime(final Flow that, final Execution execution, final JobPropertyContext context) {
        final String id = Expression.resolveBuildTime(that.getId(), context);
        final String next = Expression.resolveBuildTime(that.getNext() == null ? execution == null ? null : execution.getId() : that.getNext(), context);
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
    public FlowImpl producePartitionTime(final Flow that, final PartitionPropertyContext context) {
        final String id = Expression.resolvePartition(that.getId(), context);
        final String next = Expression.resolvePartition(that.getNext(), context);
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
