package io.machinecode.nock.core.factory.execution;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ExecutionFactory;
import io.machinecode.nock.core.factory.ListenersFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.factory.partition.PlanPartitionFactory;
import io.machinecode.nock.core.factory.task.BatchletFactory;
import io.machinecode.nock.core.factory.transition.Transitions;
import io.machinecode.nock.core.model.ListenersImpl;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.execution.BatchletPlanStepImpl;
import io.machinecode.nock.core.model.partition.PlanPartitionImpl;
import io.machinecode.nock.core.model.task.BatchletImpl;
import io.machinecode.nock.core.model.transition.TransitionImpl;
import io.machinecode.nock.jsl.api.execution.Execution;
import io.machinecode.nock.jsl.api.execution.Step;
import io.machinecode.nock.jsl.api.partition.Plan;
import io.machinecode.nock.jsl.api.task.Batchlet;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class BatchletPlanStepFactory implements ExecutionFactory<Step<Batchlet, Plan>, BatchletPlanStepImpl> {

    public static final BatchletPlanStepFactory INSTANCE = new BatchletPlanStepFactory();

    @Override
    public BatchletPlanStepImpl produceBuildTime(final Step<Batchlet, Plan> that, final Execution execution, final JobPropertyContext context) {
        final String id = Expression.resolveBuildTime(that.getId(), context);
        final String next = Expression.resolveBuildTime(that.getNext() == null ? execution == null ? null : execution.getId() : that.getNext(), context);
        final String startLimit = Expression.resolveBuildTime(that.getStartLimit(), context);
        final String allowStartIfComplete = Expression.resolveBuildTime(that.getAllowStartIfComplete(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceBuildTime(that.getProperties(), context);
        final ListenersImpl listeners = ListenersFactory.INSTANCE.produceBuildTime(that.getListeners(), context);
        final List<TransitionImpl> transitions = Transitions.immutableCopyTransitionsBuildTime(that.getTransitions(), context);
        final BatchletImpl task = that.getTask() == null ? null : BatchletFactory.INSTANCE.produceBuildTime(that.getTask(), context);
        final PlanPartitionImpl partition = that.getPartition() == null ? null : PlanPartitionFactory.INSTANCE.produceBuildTime(that.getPartition(), context);
        return new BatchletPlanStepImpl(
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
    public BatchletPlanStepImpl producePartitionTime(final Step<Batchlet, Plan> that, final PartitionPropertyContext _) {
        final PartitionPropertyContext context = new PartitionPropertyContext(); //Partition properties are step scoped
        final String id = Expression.resolvePartition(that.getId(), context);
        final String next = Expression.resolvePartition(that.getNext(), context);
        final String startLimit = Expression.resolvePartition(that.getStartLimit(), context);
        final String allowStartIfComplete = Expression.resolvePartition(that.getAllowStartIfComplete(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitionTime(that.getProperties(), context);
        final ListenersImpl listeners = ListenersFactory.INSTANCE.producePartitionTime(that.getListeners(), context);
        final List<TransitionImpl> transitions = Transitions.immutableCopyTransitionsPartitionTime(that.getTransitions(), context);
        final BatchletImpl task = that.getTask() == null ? null : BatchletFactory.INSTANCE.producePartitionTime(that.getTask(), context);
        final PlanPartitionImpl partition = that.getPartition() == null ? null : PlanPartitionFactory.INSTANCE.producePartitionTime(that.getPartition(), context);
        return new BatchletPlanStepImpl(
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
