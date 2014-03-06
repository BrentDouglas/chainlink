package io.machinecode.chainlink.repository.core;

import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StepExecutionImpl extends BaseExecutionImpl implements ExtendedStepExecution {
    private final long executionId;
    private final String stepName;

    public StepExecutionImpl(final Builder builder) {
        super(builder);
        this.executionId = builder.executionId;
        this.stepName = builder.stepName;
    }

    public StepExecutionImpl(final ExtendedStepExecution builder) {
        super(builder);
        this.executionId = builder.getStepExecutionId();
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
        builder.executionId = that.getStepExecutionId();
        builder.stepName = that.getStepName();
    }

    @Override
    public long getStepExecutionId() {
        return executionId;
    }

    @Override
    public String getStepName() {
        return stepName;
    }

    public static class Builder extends BaseExecutionImpl.Builder<Builder> {
        private long executionId;
        private String stepName;

        public Builder setStepExecutionId(final long executionId) {
            this.executionId = executionId;
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
