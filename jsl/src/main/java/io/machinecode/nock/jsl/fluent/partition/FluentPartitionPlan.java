package io.machinecode.nock.jsl.fluent.partition;

import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.partition.PartitionPlan;
import io.machinecode.nock.jsl.fluent.FluentProperties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentPartitionPlan implements PartitionPlan {

    private String partitions;
    private String threads;
    private final FluentProperties properties = new FluentProperties();


    @Override
    public String getPartitions() {
        return this.partitions;
    }

    public FluentPartitionPlan setPartitions(final String partitions) {
        this.partitions = partitions;
        return this;
    }

    @Override
    public String getThreads() {
        return this.threads;
    }

    public FluentPartitionPlan setThreads(final String threads) {
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
