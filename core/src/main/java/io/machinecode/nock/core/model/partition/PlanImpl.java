package io.machinecode.nock.core.model.partition;

import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.inject.ResolvableService;
import io.machinecode.nock.spi.element.partition.Plan;

import javax.batch.api.partition.PartitionPlan;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PlanImpl implements Plan {

    private final String partitions;
    private final String threads;
    private final PropertiesImpl properties;
    private final ResolvableService<PartitionPlan> plan;

    public PlanImpl(final String partitions, final String threads, final PropertiesImpl properties) {
        this.partitions = partitions;
        this.threads = threads;
        this.properties = properties;
        this.plan = new ResolvableService<PartitionPlan>(PartitionPlan.class);
    }

    @Override
    public String getPartitions() {
        return this.partitions;
    }

    @Override
    public String getThreads() {
        return this.threads;
    }

    @Override
    public PropertiesImpl getProperties() {
        return this.properties;
    }
}
