package io.machinecode.nock.jsl.impl.execution;

import io.machinecode.nock.jsl.api.chunk.Chunk;
import io.machinecode.nock.jsl.api.execution.Execution;
import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.partition.PartitionPlan;
import io.machinecode.nock.jsl.api.execution.Step;
import io.machinecode.nock.jsl.impl.chunk.ChunkImpl;
import io.machinecode.nock.jsl.impl.partition.PlanPartitionImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ChunkPlanStepImpl extends StepImpl<Chunk, PartitionPlan> implements Step<Chunk, PartitionPlan> {

    private final Chunk part;
    private final Partition<PartitionPlan> partition;

    public ChunkPlanStepImpl(final Step<Chunk, PartitionPlan> that, final Execution execution) {
        super(that, execution);
        this.part = that.getPart() == null ? null : new ChunkImpl(that.getPart());
        this.partition = that.getPartition() == null ? null : new PlanPartitionImpl(that.getPartition());
    }

    @Override
    public Chunk getPart() {
        return this.part;
    }

    @Override
    public Partition<PartitionPlan> getPartition() {
        return this.partition;
    }
}
