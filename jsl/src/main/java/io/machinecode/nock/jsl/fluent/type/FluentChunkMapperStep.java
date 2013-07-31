package io.machinecode.nock.jsl.fluent.type;

import io.machinecode.nock.jsl.api.chunk.Chunk;
import io.machinecode.nock.jsl.api.partition.PartitionMapper;
import io.machinecode.nock.jsl.impl.type.ChunkMapperStepImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentChunkMapperStep extends FluentStep<Chunk, PartitionMapper> {

    public ChunkMapperStepImpl build() {
        return new ChunkMapperStepImpl(this);
    }
}
