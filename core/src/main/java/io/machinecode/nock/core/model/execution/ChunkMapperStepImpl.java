package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.model.transition.TransitionImpl;
import io.machinecode.nock.jsl.api.Listeners;
import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.execution.Step;
import io.machinecode.nock.jsl.api.partition.Mapper;
import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.task.Chunk;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ChunkMapperStepImpl extends StepImpl<Chunk, Mapper> implements Step<Chunk, Mapper> {

    private final Chunk task;
    private final Partition<Mapper> partition;

    public ChunkMapperStepImpl(
            final String id,
            final String next,
            final String startLimit,
            final String allowStartIfComplete,
            final Properties properties,
            final Listeners listeners,
            final List<TransitionImpl> transitions,
            final Chunk task,
            final Partition<Mapper> partition
    ) {
        super(id, next, startLimit, allowStartIfComplete, properties, listeners, transitions);
        this.task = task;
        this.partition = partition;
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
