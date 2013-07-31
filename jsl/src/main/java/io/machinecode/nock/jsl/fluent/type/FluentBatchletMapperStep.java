package io.machinecode.nock.jsl.fluent.type;

import io.machinecode.nock.jsl.api.Batchlet;
import io.machinecode.nock.jsl.api.partition.PartitionMapper;
import io.machinecode.nock.jsl.impl.type.BatchletMapperStepImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentBatchletMapperStep extends FluentStep<Batchlet, PartitionMapper> {

    public BatchletMapperStepImpl build() {
        return new BatchletMapperStepImpl(this);
    }
}
