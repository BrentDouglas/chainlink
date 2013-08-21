package io.machinecode.nock.core.factory.execution;

import io.machinecode.nock.core.descriptor.execution.FlowImpl;
import io.machinecode.nock.core.descriptor.transition.TransitionImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ExecutionFactory;
import io.machinecode.nock.core.factory.transition.Transitions;
import io.machinecode.nock.core.work.execution.ExecutionWork;
import io.machinecode.nock.core.work.execution.FlowWork;
import io.machinecode.nock.core.work.transition.TransitionWork;
import io.machinecode.nock.spi.element.execution.Execution;
import io.machinecode.nock.spi.element.execution.Flow;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FlowFactory implements ExecutionFactory<Flow, FlowImpl, FlowWork> {

    public static final FlowFactory INSTANCE = new FlowFactory();

    @Override
    public FlowImpl produceDescriptor(final Flow that, final Execution execution, final JobPropertyContext context) {
        final String id = Expression.resolveDescriptorProperty(that.getId(), context);
        final String next = Expression.resolveDescriptorProperty(that.getNext() == null ? execution == null ? null : execution.getId() : that.getNext(), context);
        final List<Execution> executions = Executions.immutableCopyExecutionsDescriptor(that.getExecutions(), context);
        final List<TransitionImpl> transitions = Transitions.immutableCopyTransitionsDescriptor(that.getTransitions(), context);
        return new FlowImpl(
                id,
                next,
                executions,
                transitions
        );
    }

    @Override
    public FlowWork produceExecution(final FlowImpl that, final Execution execution, final JobParameterContext context) {
        final String id = Expression.resolveExecutionProperty(that.getId(), context);
        final String next = Expression.resolveExecutionProperty(that.getNext() == null ? execution == null ? null : execution.getId() : that.getNext(), context);
        final List<ExecutionWork> executions = Executions.immutableCopyExecutionsExecution(that.getExecutions(), context);
        final List<TransitionWork> transitions = Transitions.immutableCopyTransitionsExecution(that.getTransitions(), context);
        return new FlowWork(
                id,
                next,
                executions,
                transitions
        );
    }

    @Override
    public FlowWork producePartitioned(final FlowWork that, final Execution execution, final PartitionPropertyContext context) {
        final String id = Expression.resolvePartitionProperty(that.getId(), context);
        final String next = Expression.resolvePartitionProperty(that.getNext() == null ? execution == null ? null : execution.getId() : that.getNext(), context);
        final List<ExecutionWork> executions = Executions.immutableCopyExecutionsPartition(that.getExecutions(), context);
        final List<TransitionWork> transitions = Transitions.immutableCopyTransitionsPartition(that.getTransitions(), context);
        return new FlowWork(
                id,
                next,
                executions,
                transitions
        );
    }
}
