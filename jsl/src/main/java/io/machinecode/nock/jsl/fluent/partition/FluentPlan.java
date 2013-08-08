package io.machinecode.nock.jsl.fluent.partition;

import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.partition.Plan;
import io.machinecode.nock.jsl.fluent.FluentProperties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentPlan implements Plan {

    private String partitions = ONE;
    private String threads;
    private final FluentProperties properties = new FluentProperties();


    @Override
    public String getPartitions() {
        return this.partitions;
    }

    public FluentPlan setPartitions(final String partitions) {
        this.partitions = partitions;
        return this;
    }

    @Override
    public String getThreads() {
        return this.threads;
    }

    public FluentPlan setThreads(final String threads) {
        this.threads = threads;
        return this;
    }

    @Override
    public Properties getProperties() {
        return this.properties;
    }

    public FluentPlan addProperty(final String name, final String value) {
        this.properties.addProperty(name, value);
        return this;
    }
}
