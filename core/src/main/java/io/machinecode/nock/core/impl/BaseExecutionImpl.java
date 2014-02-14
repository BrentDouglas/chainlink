package io.machinecode.nock.core.impl;

import io.machinecode.nock.spi.BaseExecution;
import io.machinecode.nock.spi.Checkpoint;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.Metric;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class BaseExecutionImpl implements BaseExecution {
    private final BatchStatus batchStatus;
    private final Date start;
    private final Date updated;
    private final Date end;
    private final String exitStatus;
    private final Serializable persistentUserData;
    private final Checkpoint checkpoint;
    private final Metric[] metrics;

    public BaseExecutionImpl(final Builder builder) {
        this.batchStatus = builder.batchStatus;
        this.start = builder.start;
        this.updated = builder.updated;
        this.end = builder.end;
        this.exitStatus = builder.exitStatus;
        this.persistentUserData = builder.persistentUserData;
        this.checkpoint = builder.checkpoint;
        this.metrics = builder.metrics;
    }

    public BaseExecutionImpl(final BaseExecutionImpl builder) {
        this.batchStatus = builder.batchStatus;
        this.start = builder.start;
        this.updated = builder.updated;
        this.end = builder.end;
        this.exitStatus = builder.exitStatus;
        this.persistentUserData = builder.persistentUserData;
        this.checkpoint = builder.checkpoint;
        this.metrics = builder.metrics;
    }

    public static Builder from(final BaseExecutionImpl that) {
        final Builder builder = new Builder();
        _from(builder, that);
        return builder;
    }

    protected static void  _from(final Builder builder, final BaseExecution that) {
        builder.batchStatus = that.getBatchStatus();
        builder.start = that.getStartTime();
        builder.end = that.getEndTime();
        builder.exitStatus = that.getExitStatus();
        builder.persistentUserData = that.getPersistentUserData();
        builder.checkpoint = that.getCheckpoint();
        builder.metrics = that.getMetrics();
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
    public Date getUpdatedTime() {
        return updated;
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
    public Checkpoint getCheckpoint() {
        return checkpoint;
    }

    @Override
    public Metric[] getMetrics() {
        return metrics;
    }

    public static class Builder<T extends Builder<T>> {
        private BatchStatus batchStatus;
        private Date start;
        private Date updated;
        private Date end;
        private String exitStatus;
        private Serializable persistentUserData;
        private Checkpoint checkpoint;
        private Metric[] metrics;

        public T setBatchStatus(final BatchStatus batchStatus) {
            this.batchStatus = batchStatus;
            return (T)this;
        }

        public T setStart(final Date start) {
            this.start = start;
            return (T)this;
        }

        public T setUpdated(final Date updated) {
            this.updated = updated;
            return (T)this;
        }

        public T setEnd(final Date end) {
            this.end = end;
            return (T)this;
        }

        public T setExitStatus(final String exitStatus) {
            this.exitStatus = exitStatus;
            return (T)this;
        }

        public T setPersistentUserData(final Serializable persistentUserData) {
            this.persistentUserData = persistentUserData;
            return (T)this;
        }

        public T setCheckpoint(final Checkpoint checkpoint) {
            this.checkpoint = checkpoint;
            return (T)this;
        }

        public T setMetrics(final Metric[] metrics) {
            this.metrics = metrics;
            return (T)this;
        }
    }
}
