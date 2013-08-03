package io.machinecode.nock.jsl.impl.execution;

import io.machinecode.nock.jsl.api.partition.Plan;
import io.machinecode.nock.jsl.api.task.Batchlet;
import io.machinecode.nock.jsl.api.execution.Execution;
import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.execution.Step;
import io.machinecode.nock.jsl.impl.task.BatchletImpl;
import io.machinecode.nock.jsl.impl.partition.PlanPartitionImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class BatchletPlanStepImpl extends StepImpl<Batchlet, Plan> implements Step<Batchlet, Plan> {

    private final Batchlet task;
    private final Partition<Plan> partition;

    public BatchletPlanStepImpl(final Step<Batchlet, Plan> that, final Execution execution) {
        super(that, execution);
        this.task = that.getTask() == null ? null : new BatchletImpl(that.getTask());
        this.partition = that.getPartition() == null ? null : new PlanPartitionImpl(that.getPartition());
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
