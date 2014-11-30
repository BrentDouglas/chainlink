package io.machinecode.chainlink.core.factory.execution;

import io.machinecode.chainlink.core.element.execution.ExecutionImpl;
import io.machinecode.chainlink.core.element.execution.FlowImpl;
import io.machinecode.chainlink.core.element.transition.TransitionImpl;
import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.factory.ElementFactory;
import io.machinecode.chainlink.core.factory.transition.Transitions;
import io.machinecode.chainlink.spi.element.execution.Flow;
import io.machinecode.chainlink.spi.expression.JobPropertyContext;
import io.machinecode.chainlink.spi.expression.PropertyContext;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class FlowFactory implements ElementFactory<Flow, FlowImpl> {

    public static final FlowFactory INSTANCE = new FlowFactory();

    @Override
    public FlowImpl produceExecution(final Flow that, final JobPropertyContext context) {
        final String id = Expression.resolveExecutionProperty(that.getId(), context);
        final String next = Expression.resolveExecutionProperty(that.getNext(), context);
        final List<ExecutionImpl> executions = Executions.immutableCopyExecutionsDescriptor(that.getExecutions(), context);
        final List<TransitionImpl> transitions = Transitions.immutableCopyTransitionsDescriptor(that.getTransitions(), context);
        return new FlowImpl(
                id,
                next,
                executions,
                transitions
        );
    }

    @Override
    public FlowImpl producePartitioned(final FlowImpl that, final PropertyContext context) {
        final String id = Expression.resolvePartitionProperty(that.getId(), context);
        final String next = Expression.resolvePartitionProperty(that.getNext(), context);
        final List<ExecutionImpl> executions = Executions.immutableCopyExecutionsPartition(that.getExecutions(), context);
        final List<TransitionImpl> transitions = Transitions.immutableCopyTransitionsPartition(that.getTransitions(), context);
        return new FlowImpl(
                id,
                next,
                executions,
                transitions
        );
    }
}
