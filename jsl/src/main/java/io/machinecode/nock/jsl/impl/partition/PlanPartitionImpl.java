package io.machinecode.nock.jsl.impl.partition;

import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.partition.Plan;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PlanPartitionImpl extends PartitionImpl<Plan> implements Partition<Plan> {

    private final Plan mapper;

    public PlanPartitionImpl(final Partition<? extends Plan> that) {
        super(that);
        this.mapper = that.getStrategy() == null ? null : new PlanImpl(that.getStrategy());
    }

    @Override
    public Plan getStrategy() {
        return this.mapper;
    }
}
