package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.model.transition.TransitionImpl;
import io.machinecode.nock.jsl.api.Listeners;
import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.execution.Step;
import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.partition.Plan;
import io.machinecode.nock.jsl.api.task.Batchlet;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class BatchletPlanStepImpl extends StepImpl<Batchlet, Plan> implements Step<Batchlet, Plan> {

    private final Batchlet task;
    private final Partition<Plan> partition;

    public BatchletPlanStepImpl(
            final String id,
            final String next,
            final String startLimit,
            final String allowStartIfComplete,
            final Properties properties,
            final Listeners listeners,
            final List<TransitionImpl> transitions,
            final Batchlet task,
            final Partition<Plan> partition
    ) {
        super(id, next, startLimit, allowStartIfComplete, properties, listeners, transitions);
        this.task = task;
        this.partition = partition;
    }

    @Override
    public Batchlet getTask() {
        return this.task;
    }

    @Override
    public Partition<Plan> getPartition() {
        return this.partition;
    }
}
