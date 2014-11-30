package io.machinecode.chainlink.core.factory.execution;

import io.machinecode.chainlink.core.element.ListenersImpl;
import io.machinecode.chainlink.core.element.PropertiesImpl;
import io.machinecode.chainlink.core.element.execution.StepImpl;
import io.machinecode.chainlink.core.element.partition.PartitionImpl;
import io.machinecode.chainlink.core.element.partition.PlanImpl;
import io.machinecode.chainlink.core.element.task.ChunkImpl;
import io.machinecode.chainlink.core.element.transition.TransitionImpl;
import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.factory.ElementFactory;
import io.machinecode.chainlink.core.factory.PropertiesFactory;
import io.machinecode.chainlink.core.factory.StepListenersFactory;
import io.machinecode.chainlink.core.factory.partition.PlanPartitionFactory;
import io.machinecode.chainlink.core.factory.task.ChunkFactory;
import io.machinecode.chainlink.core.factory.transition.Transitions;
import io.machinecode.chainlink.spi.element.execution.Step;
import io.machinecode.chainlink.spi.element.partition.Plan;
import io.machinecode.chainlink.spi.element.task.Chunk;
import io.machinecode.chainlink.spi.expression.JobPropertyContext;
import io.machinecode.chainlink.spi.expression.PropertyContext;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class ChunkPlanStepFactory implements ElementFactory<Step<? extends Chunk, ? extends Plan>, StepImpl<ChunkImpl, PlanImpl>> {

    public static final ChunkPlanStepFactory INSTANCE = new ChunkPlanStepFactory();

    @Override
    public StepImpl<ChunkImpl, PlanImpl> produceExecution(final Step<? extends Chunk, ? extends Plan> that, final JobPropertyContext context) {
        final String id = Expression.resolveExecutionProperty(that.getId(), context);
        final String next = Expression.resolveExecutionProperty(that.getNext(), context);
        final String startLimit = Expression.resolveExecutionProperty(that.getStartLimit(), context);
        final String allowStartIfComplete = Expression.resolveExecutionProperty(that.getAllowStartIfComplete(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceExecution(that.getProperties(), context);
        final ListenersImpl listeners = StepListenersFactory.INSTANCE.produceExecution(that.getListeners(), context);
        final List<TransitionImpl> transitions = Transitions.immutableCopyTransitionsDescriptor(that.getTransitions(), context);
        final PartitionImpl<PlanImpl> partition = that.getPartition() == null ? null : PlanPartitionFactory.INSTANCE.produceExecution(that.getPartition(), context);
        final ChunkImpl task = that.getTask() == null ? null : ChunkFactory.INSTANCE.produceExecution(that.getTask(), listeners, partition, context);
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

    @Override
    public StepImpl<ChunkImpl, PlanImpl> producePartitioned(final StepImpl<ChunkImpl, PlanImpl> that, final PropertyContext context) {
        final String id = Expression.resolvePartitionProperty(that.getId(), context);
        final String next = Expression.resolvePartitionProperty(that.getNext(), context);
        final String startLimit = Expression.resolvePartitionProperty(that.getStartLimit(), context);
        final String allowStartIfComplete = Expression.resolvePartitionProperty(that.getAllowStartIfComplete(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitioned(that.getProperties(), context);
        final ListenersImpl listeners = StepListenersFactory.INSTANCE.producePartitioned(that.getListeners(), context);
        final List<TransitionImpl> transitions = Transitions.immutableCopyTransitionsPartition(that.getTransitions(), context);
        final PartitionImpl<PlanImpl> partition = that.getPartition() == null ? null : PlanPartitionFactory.INSTANCE.producePartitioned(that.getPartition(), context);
        final ChunkImpl task = that.getTask() == null ? null : ChunkFactory.INSTANCE.producePartitioned(that.getTask(), listeners, partition, context);
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
