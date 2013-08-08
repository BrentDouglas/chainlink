package io.machinecode.nock.core.model.partition;

import io.machinecode.nock.jsl.api.partition.Analyser;
import io.machinecode.nock.jsl.api.partition.Collector;
import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.partition.Plan;
import io.machinecode.nock.jsl.api.partition.Reducer;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PlanPartitionImpl extends PartitionImpl<Plan> implements Partition<Plan> {

    private final Plan mapper;

    public PlanPartitionImpl(final Collector collector, final Analyser analyser, final Reducer reducer, final Plan mapper) {
        super(collector, analyser, reducer);
        this.mapper = mapper;
    }

    @Override
    public Plan getStrategy() {
        return this.mapper;
    }
}
