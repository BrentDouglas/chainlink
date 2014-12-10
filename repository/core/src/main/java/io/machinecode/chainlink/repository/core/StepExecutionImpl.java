package io.machinecode.chainlink.repository.core;

import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;
import io.machinecode.chainlink.spi.repository.ExtendedStepExecutionBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class StepExecutionImpl extends BaseExecutionImpl implements ExtendedStepExecution {
    private static final long serialVersionUID = 1L;

    private final long jobExecutionId;
    private final long stepExecutionId;
    private final String stepName;

    public StepExecutionImpl(final _Builder builder) {
        super(builder);
        this.jobExecutionId = builder.jobExecutionId;
        this.stepExecutionId = builder.stepExecutionId;
        this.stepName = builder.stepName;
    }

    public StepExecutionImpl(final ExtendedStepExecution builder) {
        super(builder);
        this.jobExecutionId = builder.getJobExecutionId();
        this.stepExecutionId = builder.getStepExecutionId();
        this.stepName = builder.getStepName();
    }

    public static List<StepExecutionImpl> copy(final List<? extends ExtendedStepExecution> list) {
        final ArrayList<StepExecutionImpl> copy = new ArrayList<StepExecutionImpl>();
        for (final ExtendedStepExecution item : list) {
            copy.add(new StepExecutionImpl(item));
        }
        return copy;
    }

    public static Builder from(final ExtendedStepExecution that) {
        final Builder builder = new Builder();
        _from(builder, that);
        return builder;
    }

    protected static void  _from(final _Builder<?> builder, final ExtendedStepExecution that) {
        BaseExecutionImpl._from(builder, that);
        builder.jobExecutionId = that.getJobExecutionId();
        builder.stepExecutionId = that.getStepExecutionId();
        builder.stepName = that.getStepName();
    }

    @Override
    public long getJobExecutionId() {
        return jobExecutionId;
    }

    @Override
    public long getStepExecutionId() {
        return stepExecutionId;
    }

    @Override
    public String getStepName() {
        return stepName;
    }

    @Override
    protected void _toString(final StringBuilder sb) {
        super._toString(sb);
        sb.append(", jobExecutionId=").append(jobExecutionId);
        sb.append(", stepExecutionId=").append(stepExecutionId);
        sb.append(", stepName='").append(stepName).append('\'');
    }

    @SuppressWarnings("unchecked")
    public abstract static class _Builder<T extends _Builder<T>> extends BaseExecutionImpl._Builder<T> implements ExtendedStepExecutionBuilder<T> {
        long jobExecutionId;
        long stepExecutionId;
        String stepName;

        @Override
        public T setJobExecutionId(final long jobExecutionId) {
            this.jobExecutionId = jobExecutionId;
            return (T)this;
        }

        @Override
        public T setStepExecutionId(final long stepExecutionId) {
            this.stepExecutionId = stepExecutionId;
            return (T)this;
        }

        @Override
        public T setStepName(final String stepName) {
            this.stepName = stepName;
            return (T)this;
        }

        public abstract ExtendedStepExecution build();
    }

    public static class Builder extends _Builder<Builder> {
        @Override
        public StepExecutionImpl build() {
            return new StepExecutionImpl(this);
        }
    }
}
