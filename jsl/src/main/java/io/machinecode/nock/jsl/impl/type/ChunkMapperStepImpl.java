package io.machinecode.nock.jsl.impl.type;

import io.machinecode.nock.jsl.api.chunk.Chunk;
import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.partition.PartitionMapper;
import io.machinecode.nock.jsl.api.type.Step;
import io.machinecode.nock.jsl.impl.chunk.ChunkImpl;
import io.machinecode.nock.jsl.impl.partition.MapperPartitionImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ChunkMapperStepImpl extends StepImpl<Chunk, PartitionMapper> implements Step<Chunk, PartitionMapper> {

    private final Chunk part;
    private final Partition<PartitionMapper> partition;

    public ChunkMapperStepImpl(final Step<Chunk, PartitionMapper> that) {
        super(that);
        this.part = new ChunkImpl(that.getPart());
        this.partition = new MapperPartitionImpl(that.getPartition());
    }

    @Override
    public Chunk getPart() {
        return this.part;
    }

    @Override
    public Partition<PartitionMapper> getPartition() {
        return this.partition;
    }
}
