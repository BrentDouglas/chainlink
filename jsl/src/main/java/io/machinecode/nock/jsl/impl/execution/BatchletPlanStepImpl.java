package io.machinecode.nock.jsl.impl.execution;

import io.machinecode.nock.jsl.api.Batchlet;
import io.machinecode.nock.jsl.api.execution.Execution;
import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.partition.PartitionPlan;
import io.machinecode.nock.jsl.api.execution.Step;
import io.machinecode.nock.jsl.impl.BatchletImpl;
import io.machinecode.nock.jsl.impl.partition.PlanPartitionImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class BatchletPlanStepImpl extends StepImpl<Batchlet, PartitionPlan> implements Step<Batchlet, PartitionPlan> {

    private final Batchlet part;
    private final Partition<PartitionPlan> partition;

    public BatchletPlanStepImpl(final Step<Batchlet, PartitionPlan> that, final Execution execution) {
        super(that, execution);
        this.part = that.getPart() == null ? null : new BatchletImpl(that.getPart());
        this.partition = that.getPartition() == null ? null : new PlanPartitionImpl(that.getPartition());
    }

    @Override
    public Batchlet getPart() {
        return this.part;
    }

    @Override
    public Partition<PartitionPlan> getPartition() {
        return this.partition;
    }
}
