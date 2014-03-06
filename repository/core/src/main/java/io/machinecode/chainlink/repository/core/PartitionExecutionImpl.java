package io.machinecode.chainlink.repository.core;

import io.machinecode.chainlink.spi.PartitionExecution;

import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PartitionExecutionImpl extends BaseExecutionImpl implements PartitionExecution {
    private final long partitionExecutionId;
    private final long stepExecutionId;
    private final int partitionId;
    private final Properties partitionProperties;

    public PartitionExecutionImpl(final Builder builder) {
        super(builder);
        this.partitionExecutionId = builder.partitionExecutionId;
        this.stepExecutionId = builder.stepExecutionId;
        this.partitionId = builder.partitionId;
        this.partitionProperties = builder.partitionProperties;
    }

    public PartitionExecutionImpl(final PartitionExecution builder) {
        super(builder);
        this.partitionExecutionId = builder.getPartitionExecutionId();
        this.stepExecutionId = builder.getStepExecutionId();
        this.partitionId = builder.getPartitionId();
        this.partitionProperties = builder.getPartitionParameters();
    }

    public static PartitionExecutionImpl[] copy(final List<? extends PartitionExecution> list) {
        final PartitionExecutionImpl[] copy = new PartitionExecutionImpl[list.size()];
        for (int i = 0; i < copy.length; ++i) {
            copy[i] = new PartitionExecutionImpl(list.get(i));
        }
        return copy;
    }

    public static Builder from(final PartitionExecution that) {
        final Builder builder = new Builder();
        _from(builder, that);
        return builder;
    }

    protected static void  _from(final Builder builder, final PartitionExecution that) {
        BaseExecutionImpl._from(builder, that);
        builder.partitionExecutionId = that.getPartitionExecutionId();
        builder.stepExecutionId = that.getStepExecutionId();
        builder.partitionId = that.getPartitionId();
        builder.partitionProperties = that.getPartitionParameters();
    }

    @Override
    public long getPartitionExecutionId() {
        return partitionExecutionId;
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
    public Properties getPartitionParameters() {
        return partitionProperties;
    }

    public static class Builder extends BaseExecutionImpl.Builder<Builder> {
        private long partitionExecutionId;
        private long stepExecutionId;
        private int partitionId;
        private Properties partitionProperties;

        public Builder setPartitionExecutionId(final long partitionExecutionId) {
            this.partitionExecutionId = partitionExecutionId;
            return this;
        }

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
