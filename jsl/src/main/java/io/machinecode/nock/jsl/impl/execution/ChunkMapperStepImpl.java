package io.machinecode.nock.jsl.impl.execution;

import io.machinecode.nock.jsl.api.task.Chunk;
import io.machinecode.nock.jsl.api.execution.Execution;
import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.partition.Mapper;
import io.machinecode.nock.jsl.api.execution.Step;
import io.machinecode.nock.jsl.impl.task.ChunkImpl;
import io.machinecode.nock.jsl.impl.partition.MapperPartitionImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ChunkMapperStepImpl extends StepImpl<Chunk, Mapper> implements Step<Chunk, Mapper> {

    private final Chunk task;
    private final Partition<Mapper> partition;

    public ChunkMapperStepImpl(final Step<Chunk, Mapper> that, final Execution execution) {
        super(that, execution);
        this.task = that.getTask() == null ? null : new ChunkImpl(that.getTask());
        this.partition = that.getPartition() == null ? null : new MapperPartitionImpl(that.getPartition());
    }

    @Override
    public Chunk getTask() {
        return this.task;
    }

    @Override
    public Partition<Mapper> getPartition() {
        return this.partition;
    }
}
