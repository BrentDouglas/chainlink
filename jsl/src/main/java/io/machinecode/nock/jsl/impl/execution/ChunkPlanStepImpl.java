package io.machinecode.nock.jsl.impl.execution;

import io.machinecode.nock.jsl.api.partition.Plan;
import io.machinecode.nock.jsl.api.task.Chunk;
import io.machinecode.nock.jsl.api.execution.Execution;
import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.execution.Step;
import io.machinecode.nock.jsl.impl.task.ChunkImpl;
import io.machinecode.nock.jsl.impl.partition.PlanPartitionImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ChunkPlanStepImpl extends StepImpl<Chunk, Plan> implements Step<Chunk, Plan> {

    private final Chunk task;
    private final Partition<Plan> partition;

    public ChunkPlanStepImpl(final Step<Chunk, Plan> that, final Execution execution) {
        super(that, execution);
        this.task = that.getTask() == null ? null : new ChunkImpl(that.getTask());
        this.partition = that.getPartition() == null ? null : new PlanPartitionImpl(that.getPartition());
    }

    @Override
    public Chunk getTask() {
        return this.task;
    }

    @Override
    public Partition<Plan> getPartition() {
        return this.partition;
    }
}
