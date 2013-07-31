package io.machinecode.nock.jsl.fluent.type;

import io.machinecode.nock.jsl.api.Batchlet;
import io.machinecode.nock.jsl.api.partition.PartitionPlan;
import io.machinecode.nock.jsl.impl.type.BatchletPlanStepImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentBatchletPlanStep extends FluentStep<Batchlet, PartitionPlan> {

    public BatchletPlanStepImpl build() {
        return new BatchletPlanStepImpl(this);
    }
}
