package io.machinecode.nock.jsl.impl.type;

import io.machinecode.nock.jsl.api.chunk.Chunk;
import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.partition.PartitionPlan;
import io.machinecode.nock.jsl.api.type.Step;
import io.machinecode.nock.jsl.impl.chunk.ChunkImpl;
import io.machinecode.nock.jsl.impl.partition.PlanPartitionImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ChunkPlanStepImpl extends StepImpl<Chunk, PartitionPlan> implements Step<Chunk, PartitionPlan> {

    private final Chunk part;
    private final Partition<PartitionPlan> partition;

    public ChunkPlanStepImpl(final Step<Chunk, PartitionPlan> that) {
        super(that);
        this.part = new ChunkImpl(that.getPart());
        this.partition = new PlanPartitionImpl(that.getPartition());
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
