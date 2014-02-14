package io.machinecode.nock.core.impl;

import io.machinecode.nock.spi.ExtendedStepExecution;
import io.machinecode.nock.spi.PartitionExecution;

import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PartitionExecutionImpl extends BaseExecutionImpl implements PartitionExecution {
    private final long stepExecutionId;
    private final int partitionId;
    private final Properties partitionProperties;

    public PartitionExecutionImpl(final Builder builder) {
        super(builder);
        this.stepExecutionId = builder.stepExecutionId;
        this.partitionId = builder.partitionId;
        this.partitionProperties = builder.partitionProperties;
    }

    public PartitionExecutionImpl(final PartitionExecutionImpl builder) {
        super(builder);
        this.stepExecutionId = builder.stepExecutionId;
        this.partitionId = builder.partitionId;
        this.partitionProperties = builder.partitionProperties;
    }

    public static Builder from(final PartitionExecution that) {
        final Builder builder = new Builder();
        _from(builder, that);
        return builder;
    }

    protected static void  _from(final Builder builder, final PartitionExecution that) {
        BaseExecutionImpl._from(builder, that);
        builder.stepExecutionId = that.getStepExecutionId();
        builder.partitionId = that.getPartitionId();
        builder.partitionProperties = that.getPartitionProperties();
    }

    @Override
    public long getStepExecutionId() {
        return stepExecutionId;
    }

    @Override
    public int getPartitionId() {
        return partitionId;
    }

    @Override
    public Properties getPartitionProperties() {
        return partitionProperties;
    }

    public static class Builder extends BaseExecutionImpl.Builder<Builder> {
        private long stepExecutionId;
        private int partitionId;
        private Properties partitionProperties;

        public Builder setStepExecutionId(final long stepExecutionId) {
            this.stepExecutionId = stepExecutionId;
            return this;
        }

        public Builder setPartitionId(final int partitionId) {
            this.partitionId = partitionId;
            return this;
        }

        public Builder setPartitionProperties(final Properties partitionProperties) {
            this.partitionProperties = partitionProperties;
            return this;
        }

        public PartitionExecutionImpl build() {
            return new PartitionExecutionImpl(this);
        }
    }
}
