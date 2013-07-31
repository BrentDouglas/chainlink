package io.machinecode.nock.jsl.fluent.type;

import io.machinecode.nock.jsl.api.chunk.Chunk;
import io.machinecode.nock.jsl.api.partition.PartitionPlan;
import io.machinecode.nock.jsl.impl.type.ChunkPlanStepImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentChunkPlanStep extends FluentStep<Chunk, PartitionPlan> {

    public ChunkPlanStepImpl build() {
        return new ChunkPlanStepImpl(this);
    }
}
