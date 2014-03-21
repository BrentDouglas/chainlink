package io.machinecode.chainlink.repository.core;

import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StepExecutionImpl extends BaseExecutionImpl implements ExtendedStepExecution {
    private final long jobExecutionId;
    private final long stepExecutionId;
    private final String stepName;

    public StepExecutionImpl(final Builder builder) {
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

    protected static void  _from(final Builder builder, final ExtendedStepExecution that) {
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

    public static class Builder extends BaseExecutionImpl.Builder<Builder> {
        private long jobExecutionId;
        private long stepExecutionId;
        private String stepName;

        public Builder setJobExecutionId(final long jobExecutionId) {
            this.jobExecutionId = jobExecutionId;
            return this;
        }

        public Builder setStepExecutionId(final long stepExecutionId) {
            this.stepExecutionId = stepExecutionId;
            return this;
        }

        public Builder setStepName(final String stepName) {
            this.stepName = stepName;
            return this;
        }

        public StepExecutionImpl build() {
            return new StepExecutionImpl(this);
        }
    }
}
