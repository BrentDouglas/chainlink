package io.machinecode.nock.core.model.partition;

import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.spi.element.partition.Plan;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.work.StrategyWork;

import javax.batch.api.partition.PartitionPlan;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PlanImpl implements Plan, StrategyWork {

    private final String partitions;
    private final String threads;
    private final PropertiesImpl properties;

    public PlanImpl(final String partitions, final String threads, final PropertiesImpl properties) {
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
    public PropertiesImpl getProperties() {
        return this.properties;
    }

    @Override
    public PartitionPlan getPartitionPlan(final InjectionContext context) {
        return new PartitionPlanImpl(
                Integer.parseInt(this.partitions),
                Integer.parseInt(this.threads),
                this.properties.toArray(new Properties[this.properties.size()])
        );
    }

    private static class PartitionPlanImpl implements PartitionPlan {
        private int partitions;
        private boolean override; //Where does this come from
        private int threads;
        private Properties[] properties;

        public PartitionPlanImpl() { }

        public PartitionPlanImpl(final int partitions, final int threads, final Properties[] properties) {
            this.partitions = partitions;
            this.threads = threads;
            this.properties = properties;
        }

        @Override
        public boolean getPartitionsOverride() {
            return this.override;
        }

        @Override
        public void setPartitionsOverride(final boolean override) {
            this.override = override;
        }

        @Override
        public int getPartitions() {
            return this.partitions;
        }

        @Override
        public void setPartitions(final int partitions) {
            this.partitions = partitions;
        }

        @Override
        public int getThreads() {
            return this.threads;
        }

        @Override
        public void setThreads(final int threads) {
            this.threads = threads;
        }

        @Override
        public Properties[] getPartitionProperties() {
            return this.properties;
        }

        @Override
        public void setPartitionProperties(final Properties[] properties) {
            this.properties = properties;
        }
    }
}
