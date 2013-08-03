package io.machinecode.nock.jsl.impl.execution;

import io.machinecode.nock.jsl.api.task.Batchlet;
import io.machinecode.nock.jsl.api.execution.Execution;
import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.partition.Mapper;
import io.machinecode.nock.jsl.api.execution.Step;
import io.machinecode.nock.jsl.impl.task.BatchletImpl;
import io.machinecode.nock.jsl.impl.partition.MapperPartitionImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class BatchletMapperStepImpl extends StepImpl<Batchlet, Mapper> implements Step<Batchlet, Mapper> {

    private final Batchlet task;
    private final Partition<Mapper> partition;

    public BatchletMapperStepImpl(final Step<Batchlet, Mapper> that, final Execution execution) {
        super(that, execution);
        this.task = that.getTask() == null ? null : new BatchletImpl(that.getTask());
        this.partition = that.getPartition() == null ? null : new MapperPartitionImpl(that.getPartition());
    }

    @Override
    public Batchlet getTask() {
        return this.task;
    }

    @Override
    public Partition<Mapper> getPartition() {
        return this.partition;
    }
}
