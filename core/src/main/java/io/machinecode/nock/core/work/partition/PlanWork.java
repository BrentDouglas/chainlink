package io.machinecode.nock.core.work.partition;

import io.machinecode.nock.core.inject.ResolvableService;
import io.machinecode.nock.core.work.Work;
import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.partition.Plan;

import javax.batch.api.partition.PartitionPlan;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PlanWork implements Work, Plan {

    private final String partitions;
    private final String threads;
    private final ResolvableService<PartitionPlan> plan;

    public PlanWork(final String partitions, final String threads) {
        this.partitions = partitions;
        this.threads = threads;
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
    public Properties getProperties() {
        return null;
    }
}
