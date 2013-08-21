package io.machinecode.nock.core.descriptor.partition;

import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.partition.Plan;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PlanImpl implements Plan {

    private final String partitions;
    private final String threads;
    private final Properties properties;

    public PlanImpl(final String partitions, final String threads, final Properties properties) {
        this.partitions = partitions;
        this.threads = threads;
        this.properties = properties;
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
        return this.properties;
    }
}
