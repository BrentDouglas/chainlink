package io.machinecode.chainlink.core.factory.execution;

import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.core.expression.PartitionPropertyContext;
import io.machinecode.chainlink.core.factory.PropertiesFactory;
import io.machinecode.chainlink.core.factory.StepListenersFactory;
import io.machinecode.chainlink.core.factory.partition.PlanPartitionFactory;
import io.machinecode.chainlink.core.factory.task.ChunkFactory;
import io.machinecode.chainlink.core.factory.transition.Transitions;
import io.machinecode.chainlink.core.jsl.impl.ListenersImpl;
import io.machinecode.chainlink.core.jsl.impl.PropertiesImpl;
import io.machinecode.chainlink.core.jsl.impl.execution.StepImpl;
import io.machinecode.chainlink.core.jsl.impl.partition.PartitionImpl;
import io.machinecode.chainlink.core.jsl.impl.partition.PlanImpl;
import io.machinecode.chainlink.core.jsl.impl.task.ChunkImpl;
import io.machinecode.chainlink.core.jsl.impl.transition.TransitionImpl;
import io.machinecode.chainlink.spi.jsl.execution.Step;
import io.machinecode.chainlink.spi.jsl.partition.Plan;
import io.machinecode.chainlink.spi.jsl.task.Chunk;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ChunkPlanStepFactory {

    public static StepImpl<ChunkImpl, PlanImpl> produceExecution(final Step<? extends Chunk, ? extends Plan> that, final JobPropertyContext context) {
        final String id = Expression.resolveExecutionProperty(that.getId(), context);
        final String next = Expression.resolveExecutionProperty(that.getNext(), context);
        final String startLimit = Expression.resolveExecutionProperty(that.getStartLimit(), context);
        final String allowStartIfComplete = Expression.resolveExecutionProperty(that.getAllowStartIfComplete(), context);
        final PropertiesImpl properties = PropertiesFactory.produceExecution(that.getProperties(), context);
        final ListenersImpl listeners = StepListenersFactory.produceExecution(that.getListeners(), context);
        final List<TransitionImpl> transitions = Transitions.immutableCopyTransitionsDescriptor(that.getTransitions(), context);
        final PartitionImpl<PlanImpl> partition = that.getPartition() == null ? null : PlanPartitionFactory.produceExecution(that.getPartition(), context);
        final ChunkImpl task = that.getTask() == null ? null : ChunkFactory.produceExecution(that.getTask(), listeners, partition, context);
        return new StepImpl<ChunkImpl, PlanImpl>(
                id,
                next,
                startLimit,
                allowStartIfComplete,
                properties,
                listeners,
                transitions,
                task,
                partition
        );
    }

    public static StepImpl<ChunkImpl, PlanImpl> producePartitioned(final StepImpl<ChunkImpl, PlanImpl> that, final PartitionPropertyContext context) {
        final String id = Expression.resolvePartitionProperty(that.getId(), context);
        final String next = Expression.resolvePartitionProperty(that.getNext(), context);
        final String startLimit = Expression.resolvePartitionProperty(that.getStartLimit(), context);
        final String allowStartIfComplete = Expression.resolvePartitionProperty(that.getAllowStartIfComplete(), context);
        final PropertiesImpl properties = PropertiesFactory.producePartitioned(that.getProperties(), context);
        final ListenersImpl listeners = StepListenersFactory.producePartitioned(that.getListeners(), context);
        final List<TransitionImpl> transitions = Transitions.immutableCopyTransitionsPartition(that.getTransitions(), context);
        final PartitionImpl<PlanImpl> partition = that.getPartition() == null ? null : PlanPartitionFactory.producePartitioned(that.getPartition(), context);
        final ChunkImpl task = that.getTask() == null ? null : ChunkFactory.producePartitioned(that.getTask(), listeners, partition, context);
        return new StepImpl<ChunkImpl, PlanImpl>(
                id,
                next,
                startLimit,
                allowStartIfComplete,
                properties,
                listeners,
                transitions,
                task,
                partition
        );
    }
}
