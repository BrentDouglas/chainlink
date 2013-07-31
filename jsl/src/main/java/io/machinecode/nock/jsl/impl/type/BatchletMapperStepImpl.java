package io.machinecode.nock.jsl.impl.type;

import io.machinecode.nock.jsl.api.Batchlet;
import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.partition.PartitionMapper;
import io.machinecode.nock.jsl.api.type.Step;
import io.machinecode.nock.jsl.impl.BatchletImpl;
import io.machinecode.nock.jsl.impl.partition.MapperPartitionImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class BatchletMapperStepImpl extends StepImpl<Batchlet, PartitionMapper> implements Step<Batchlet, PartitionMapper> {

    private final Batchlet part;
    private final Partition<PartitionMapper> partition;

    public BatchletMapperStepImpl(final Step<Batchlet, PartitionMapper> that) {
        super(that);
        this.part = new BatchletImpl(that.getPart());
        this.partition = new MapperPartitionImpl(that.getPartition());
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
