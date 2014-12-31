package io.machinecode.chainlink.core.repository;

import io.machinecode.chainlink.spi.repository.PartitionExecution;
import io.machinecode.chainlink.spi.repository.PartitionExecutionBuilder;

import java.util.List;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class PartitionExecutionImpl extends BaseExecutionImpl implements PartitionExecution {
    private static final long serialVersionUID = 1L;

    private final long partitionExecutionId;
    private final long stepExecutionId;
    private final int partitionId;
    private final Properties partitionParameters;

    public PartitionExecutionImpl(final _Builder builder) {
        super(builder);
        this.partitionExecutionId = builder.partitionExecutionId;
        this.stepExecutionId = builder.stepExecutionId;
        this.partitionId = builder.partitionId;
        this.partitionParameters = builder.partitionParameters;
    }

    public PartitionExecutionImpl(final PartitionExecution builder) {
        super(builder);
        this.partitionExecutionId = builder.getPartitionExecutionId();
        this.stepExecutionId = builder.getStepExecutionId();
        this.partitionId = builder.getPartitionId();
        this.partitionParameters = builder.getPartitionParameters();
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

    protected static void  _from(final _Builder<?> builder, final PartitionExecution that) {
        BaseExecutionImpl._from(builder, that);
        builder.partitionExecutionId = that.getPartitionExecutionId();
        builder.stepExecutionId = that.getStepExecutionId();
        builder.partitionId = that.getPartitionId();
        builder.partitionParameters = that.getPartitionParameters();
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
        return partitionParameters;
    }

    @Override
    protected void _toString(final StringBuilder sb) {
        super._toString(sb);
        sb.append(", partitionExecutionId=").append(partitionExecutionId);
        sb.append(", stepExecutionId=").append(stepExecutionId);
        sb.append(", partitionId=").append(partitionId);
        sb.append(", partitionParameters=").append(partitionParameters);
    }

    @SuppressWarnings("unchecked")
    public abstract static class _Builder<T extends _Builder<T>> extends BaseExecutionImpl._Builder<T> implements PartitionExecutionBuilder<T> {
        long partitionExecutionId;
        long stepExecutionId;
        int partitionId;
        Properties partitionParameters;

        @Override
        public T setPartitionExecutionId(final long partitionExecutionId) {
            this.partitionExecutionId = partitionExecutionId;
            return (T)this;
        }

        @Override
        public T setStepExecutionId(final long stepExecutionId) {
            this.stepExecutionId = stepExecutionId;
            return (T)this;
        }

        @Override
        public T setPartitionId(final int partitionId) {
            this.partitionId = partitionId;
            return (T)this;
        }

        @Override
        public T setPartitionParameters(final Properties partitionParameters) {
            this.partitionParameters = partitionParameters;
            return (T)this;
        }

        public abstract PartitionExecution build();
    }

    public static class Builder extends _Builder<Builder> {
        @Override
        public PartitionExecutionImpl build() {
            return new PartitionExecutionImpl(this);
        }
    }
}
