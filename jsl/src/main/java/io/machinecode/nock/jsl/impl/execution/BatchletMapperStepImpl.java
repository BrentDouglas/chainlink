package io.machinecode.nock.jsl.impl.execution;

import io.machinecode.nock.jsl.api.Batchlet;
import io.machinecode.nock.jsl.api.execution.Execution;
import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.partition.PartitionMapper;
import io.machinecode.nock.jsl.api.execution.Step;
import io.machinecode.nock.jsl.impl.BatchletImpl;
import io.machinecode.nock.jsl.impl.partition.MapperPartitionImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class BatchletMapperStepImpl extends StepImpl<Batchlet, PartitionMapper> implements Step<Batchlet, PartitionMapper> {

    private final Batchlet part;
    private final Partition<PartitionMapper> partition;

    public BatchletMapperStepImpl(final Step<Batchlet, PartitionMapper> that, final Execution execution) {
        super(that, execution);
        this.part = that.getPart() == null ? null : new BatchletImpl(that.getPart());
        this.partition = that.getPartition() == null ? null : new MapperPartitionImpl(that.getPartition());
    }

    @Override
    public Batchlet getPart() {
        return this.part;
    }

    @Override
    public Partition<PartitionMapper> getPartition() {
        return this.partition;
    }
}
