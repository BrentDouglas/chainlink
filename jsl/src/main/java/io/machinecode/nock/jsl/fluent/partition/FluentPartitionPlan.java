package io.machinecode.nock.jsl.fluent.partition;

import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.partition.PartitionPlan;
import io.machinecode.nock.jsl.fluent.FluentProperties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentPartitionPlan implements PartitionPlan {

    private int partitions = 1;
    private Integer threads;
    private final FluentProperties properties = new FluentProperties();


    @Override
    public int getPartitions() {
        return this.partitions;
    }

    public FluentPartitionPlan setPartitions(final int partitions) {
        this.partitions = partitions;
        return this;
    }

    @Override
    public Integer getThreads() {
        return this.threads;
    }

    public FluentPartitionPlan setThreads(final Integer threads) {
        this.threads = threads;
        return this;
    }

    @Override
    public Properties getProperties() {
        return this.properties;
    }

    public FluentPartitionPlan addProperty(final String name, final String value) {
        this.properties.addProperty(name, value);
        return this;
    }
}
