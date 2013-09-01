package io.machinecode.nock.core.impl;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.Metric;
import javax.batch.runtime.StepExecution;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StepExecutionImpl implements StepExecution {
    private final long executionId;
    private final String stepName;
    private final BatchStatus batchStatus;
    private final Date start;
    private final Date end;
    private final String exitStatus;
    private final Serializable persistentUserData;
    private final Metric[] metrics;

    public StepExecutionImpl(final Builder builder) {
        this.executionId = builder.executionId;
        this.stepName = builder.stepName;
        this.batchStatus = builder.batchStatus;
        this.start = builder.start;
        this.end = builder.end;
        this.exitStatus = builder.exitStatus;
        this.persistentUserData = builder.persistentUserData;
        this.metrics = builder.metrics;
    }

    public StepExecutionImpl(final StepExecutionImpl builder) {
        this.executionId = builder.executionId;
        this.stepName = builder.stepName;
        this.batchStatus = builder.batchStatus;
        this.start = builder.start;
        this.end = builder.end;
        this.exitStatus = builder.exitStatus;
        this.persistentUserData = builder.persistentUserData;
        this.metrics = builder.metrics;
    }

    @Override
    public long getStepExecutionId() {
        return executionId;
    }

    @Override
    public String getStepName() {
        return stepName;
    }

    @Override
    public BatchStatus getBatchStatus() {
        return batchStatus;
    }

    @Override
    public Date getStartTime() {
        return start;
    }

    @Override
    public Date getEndTime() {
        return end;
    }

    @Override
    public String getExitStatus() {
        return exitStatus;
    }

    @Override
    public Serializable getPersistentUserData() {
        return persistentUserData;
    }

    @Override
    public Metric[] getMetrics() {
        return metrics;
    }

    public static Builder from(final StepExecution that) {
        final Builder builder = new Builder();
        builder.executionId = that.getStepExecutionId();
        builder.stepName = that.getStepName();
        builder.batchStatus = that.getBatchStatus();
        builder.start = that.getStartTime();
        builder.end = that.getEndTime();
        builder.exitStatus = that.getExitStatus();
        builder.persistentUserData = that.getPersistentUserData();
        builder.metrics = that.getMetrics();
        return builder;
    }

    public static class Builder {
        private long executionId;
        private String stepName;
        private BatchStatus batchStatus;
        private Date start;
        private Date end;
        private String exitStatus;
        private Serializable persistentUserData;
        private Metric[] metrics;

        public Builder setExecutionId(final long executionId) {
            this.executionId = executionId;
            return this;
        }

        public Builder setStepName(final String stepName) {
            this.stepName = stepName;
            return this;
        }

        public Builder setBatchStatus(final BatchStatus batchStatus) {
            this.batchStatus = batchStatus;
            return this;
        }

        public Builder setStart(final Date start) {
            this.start = start;
            return this;
        }

        public Builder setEnd(final Date end) {
            this.end = end;
            return this;
        }

        public Builder setExitStatus(final String exitStatus) {
            this.exitStatus = exitStatus;
            return this;
        }

        public Builder setPersistentUserData(final Serializable persistentUserData) {
            this.persistentUserData = persistentUserData;
            return this;
        }

        public Builder setMetrics(final Metric... metrics) {
            this.metrics = metrics;
            return this;
        }

        public StepExecutionImpl build() {
            return new StepExecutionImpl(this);
        }
    }
}
