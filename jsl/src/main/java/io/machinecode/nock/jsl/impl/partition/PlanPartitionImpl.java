package io.machinecode.nock.jsl.impl.partition;

import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.partition.PartitionPlan;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PlanPartitionImpl extends PartitionImpl<PartitionPlan> implements Partition<PartitionPlan> {

    private final PartitionPlan mapper;

    public PlanPartitionImpl(final Partition<PartitionPlan> that) {
        super(that);
        this.mapper = new PartitionPlanImpl(that.getMapper());
    }

    @Override
    public PartitionPlan getMapper() {
        return this.mapper;
    }
}
