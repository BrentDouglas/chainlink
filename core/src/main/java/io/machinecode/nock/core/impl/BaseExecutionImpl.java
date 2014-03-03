package io.machinecode.nock.core.impl;

import io.machinecode.nock.spi.BaseExecution;

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
    private final Metric[] metrics;
    private final Serializable readerCheckpoint;
    private final Serializable writerCheckpoint;

    public BaseExecutionImpl(final Builder builder) {
        this.batchStatus = builder.batchStatus;
        this.start = builder.start;
        this.updated = builder.updated;
        this.end = builder.end;
        this.exitStatus = builder.exitStatus;
        this.persistentUserData = builder.persistentUserData;
        this.metrics = builder.metrics;
        this.readerCheckpoint = builder.readerCheckpoint;
        this.writerCheckpoint = builder.writerCheckpoint;
    }

    public BaseExecutionImpl(final BaseExecutionImpl builder) {
        this.batchStatus = builder.batchStatus;
        this.start = builder.start;
        this.updated = builder.updated;
        this.end = builder.end;
        this.exitStatus = builder.exitStatus;
        this.persistentUserData = builder.persistentUserData;
        this.metrics = builder.metrics;
        this.readerCheckpoint = builder.readerCheckpoint;
        this.writerCheckpoint = builder.writerCheckpoint;
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
        builder.metrics = that.getMetrics();
        builder.readerCheckpoint = that.getReaderCheckpoint();
        builder.writerCheckpoint = that.getWriterCheckpoint();
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
    public Metric[] getMetrics() {
        return metrics;
    }

    @Override
    public Serializable getReaderCheckpoint() {
        return readerCheckpoint;
    }

    @Override
    public Serializable getWriterCheckpoint() {
        return writerCheckpoint;
    }

    public static class Builder<T extends Builder<T>> {
        private BatchStatus batchStatus;
        private Date start;
        private Date updated;
        private Date end;
        private String exitStatus;
        private Serializable persistentUserData;
        private Metric[] metrics;
        private Serializable readerCheckpoint;
        private Serializable writerCheckpoint;

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

        public T setMetrics(final Metric[] metrics) {
            this.metrics = metrics;
            return (T)this;
        }

        public T setReaderCheckpoint(final Serializable reader) {
            this.readerCheckpoint = reader;
            return (T)this;
        }

        public T setWriterCheckpoint(final Serializable writer) {
            this.writerCheckpoint = writer;
            return (T)this;
        }
    }
}
