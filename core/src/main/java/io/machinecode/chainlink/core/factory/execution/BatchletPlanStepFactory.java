package io.machinecode.chainlink.core.factory.execution;

import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.factory.PropertiesFactory;
import io.machinecode.chainlink.core.factory.StepListenersFactory;
import io.machinecode.chainlink.core.factory.partition.PlanPartitionFactory;
import io.machinecode.chainlink.core.factory.task.BatchletFactory;
import io.machinecode.chainlink.core.factory.transition.Transitions;
import io.machinecode.chainlink.core.model.ListenersImpl;
import io.machinecode.chainlink.core.model.PropertiesImpl;
import io.machinecode.chainlink.core.model.execution.StepImpl;
import io.machinecode.chainlink.core.model.partition.PartitionImpl;
import io.machinecode.chainlink.core.model.partition.PlanImpl;
import io.machinecode.chainlink.core.model.task.BatchletImpl;
import io.machinecode.chainlink.core.model.transition.TransitionImpl;
import io.machinecode.chainlink.spi.element.execution.Step;
import io.machinecode.chainlink.spi.element.partition.Plan;
import io.machinecode.chainlink.spi.element.task.Batchlet;
import io.machinecode.chainlink.spi.factory.ElementFactory;
import io.machinecode.chainlink.spi.factory.JobPropertyContext;
import io.machinecode.chainlink.spi.factory.PropertyContext;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class BatchletPlanStepFactory implements ElementFactory<Step<? extends Batchlet, ? extends Plan>, StepImpl<BatchletImpl, PlanImpl>> {

    public static final BatchletPlanStepFactory INSTANCE = new BatchletPlanStepFactory();

    @Override
    public StepImpl<BatchletImpl, PlanImpl> produceExecution(final Step<? extends Batchlet, ? extends Plan> that, final JobPropertyContext context) {
        final String id = Expression.resolveExecutionProperty(that.getId(), context);
        final String next = Expression.resolveExecutionProperty(that.getNext(), context);
        final String startLimit = Expression.resolveExecutionProperty(that.getStartLimit(), context);
        final String allowStartIfComplete = Expression.resolveExecutionProperty(that.getAllowStartIfComplete(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceExecution(that.getProperties(), context);
        final ListenersImpl listeners = StepListenersFactory.INSTANCE.produceExecution(that.getListeners(), context);
        final List<TransitionImpl> transitions = Transitions.immutableCopyTransitionsDescriptor(that.getTransitions(), context);
        final PartitionImpl<PlanImpl> partition = that.getPartition() == null ? null : PlanPartitionFactory.INSTANCE.produceExecution(that.getPartition(), context);
        final BatchletImpl task = that.getTask() == null ? null : BatchletFactory.INSTANCE.produceExecution(that.getTask(), listeners, partition, context);
        return new StepImpl<BatchletImpl, PlanImpl>(
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
    public StepImpl<BatchletImpl, PlanImpl> producePartitioned(final StepImpl<BatchletImpl, PlanImpl> that, final PropertyContext context) {
        final String id = Expression.resolvePartitionProperty(that.getId(), context);
        final String next = Expression.resolvePartitionProperty(that.getNext(), context);
        final String startLimit = Expression.resolvePartitionProperty(that.getStartLimit(), context);
        final String allowStartIfComplete = Expression.resolvePartitionProperty(that.getAllowStartIfComplete(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitioned(that.getProperties(), context);
        final ListenersImpl listeners = StepListenersFactory.INSTANCE.producePartitioned(that.getListeners(), context);
        final List<TransitionImpl> transitions = Transitions.immutableCopyTransitionsPartition(that.getTransitions(), context);
        final PartitionImpl<PlanImpl> partition = that.getPartition() == null ? null : PlanPartitionFactory.INSTANCE.producePartitioned(that.getPartition(), context);
        final BatchletImpl task = that.getTask() == null ? null : BatchletFactory.INSTANCE.producePartitioned(that.getTask(), listeners, partition, context);
        return new StepImpl<BatchletImpl, PlanImpl>(
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
