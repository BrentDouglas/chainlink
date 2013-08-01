package io.machinecode.nock.jsl.impl.partition;

import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.partition.PartitionPlan;
import io.machinecode.nock.jsl.impl.PropertiesImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PartitionPlanImpl implements PartitionPlan {

    private final int partitions;
    private final int threads;
    private final Properties properties;

    public PartitionPlanImpl(final PartitionPlan that) {
        this.partitions = that.getPartitions();
        this.threads = that.getThreads() == null ? that.getPartitions() : that.getThreads();
        this.properties = new PropertiesImpl(that.getProperties());
    }

    @Override
    public int getPartitions() {
        return this.partitions;
    }

    @Override
    public Integer getThreads() {
        return this.threads;
    }

    @Override
    public Properties getProperties() {
        return this.properties;
    }
}
