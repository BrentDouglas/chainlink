package io.machinecode.chainlink.repository.core;

import io.machinecode.chainlink.spi.repository.BaseExecution;
import io.machinecode.chainlink.spi.repository.BaseExecutionBuilder;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.Metric;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public abstract class BaseExecutionImpl implements BaseExecution, Serializable {
    private final BatchStatus batchStatus;
    private final Date createTime;
    private final Date startTime;
    private final Date updatedTime;
    private final Date endTime;
    private final String exitStatus;
    private final Serializable persistentUserData;
    private final Metric[] metrics;
    private final Serializable readerCheckpoint;
    private final Serializable writerCheckpoint;

    public BaseExecutionImpl(final _Builder builder) {
        this.batchStatus = builder.batchStatus;
        this.createTime = builder.createTime;
        this.startTime = builder.startTime;
        this.updatedTime = builder.updatedTime;
        this.endTime = builder.endTime;
        this.exitStatus = builder.exitStatus;
        this.persistentUserData = builder.persistentUserData;
        this.metrics = builder.metrics;
        this.readerCheckpoint = builder.readerCheckpoint;
        this.writerCheckpoint = builder.writerCheckpoint;
    }

    public BaseExecutionImpl(final BaseExecution builder) {
        this.batchStatus = builder.getBatchStatus();
        this.createTime = builder.getCreateTime();
        this.startTime = builder.getStartTime();
        this.updatedTime = builder.getUpdatedTime();
        this.endTime = builder.getEndTime();
        this.exitStatus = builder.getExitStatus();
        this.persistentUserData = builder.getPersistentUserData();
        this.metrics = builder.getMetrics();
        this.readerCheckpoint = builder.getReaderCheckpoint();
        this.writerCheckpoint = builder.getWriterCheckpoint();
    }

    protected static void  _from(final _Builder<?> builder, final BaseExecution that) {
        builder.batchStatus = that.getBatchStatus();
        builder.createTime = that.getCreateTime();
        builder.startTime = that.getStartTime();
        builder.endTime = that.getEndTime();
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
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public Date getStartTime() {
        return startTime;
    }

    @Override
    public Date getUpdatedTime() {
        return updatedTime;
    }

    @Override
    public Date getEndTime() {
        return endTime;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append("{");
        _toString(sb);
        sb.append('}');
        return sb.toString();
    }

    protected void _toString(final StringBuilder sb) {
        sb.append("batchStatus=").append(batchStatus);
        sb.append(", createTime=").append(createTime);
        sb.append(", startTime=").append(startTime);
        sb.append(", updatedTime=").append(updatedTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", exitStatus='").append(exitStatus).append('\'');
        sb.append(", persistentUserData=").append(persistentUserData);
        sb.append(", metrics=").append(Arrays.toString(metrics));
        sb.append(", readerCheckpoint=").append(readerCheckpoint);
        sb.append(", writerCheckpoint=").append(writerCheckpoint);
    }

    @SuppressWarnings("unchecked")
    public abstract static class _Builder<T extends _Builder<T>> implements BaseExecutionBuilder<T> {
        private BatchStatus batchStatus;
        private Date createTime;
        private Date startTime;
        private Date updatedTime;
        private Date endTime;
        private String exitStatus;
        private Serializable persistentUserData;
        private Metric[] metrics;
        private Serializable readerCheckpoint;
        private Serializable writerCheckpoint;

        @Override
        public T setBatchStatus(final BatchStatus batchStatus) {
            this.batchStatus = batchStatus;
            return (T)this;
        }

        @Override
        public T setCreateTime(final Date create) {
            this.createTime = create;
            return (T)this;
        }

        @Override
        public T setStartTime(final Date start) {
            this.startTime = start;
            return (T)this;
        }

        @Override
        public T setUpdatedTime(final Date updated) {
            this.updatedTime = updated;
            return (T)this;
        }

        @Override
        public T setEndTime(final Date end) {
            this.endTime = end;
            return (T)this;
        }

        @Override
        public T setExitStatus(final String exitStatus) {
            this.exitStatus = exitStatus;
            return (T)this;
        }

        @Override
        public T setPersistentUserData(final Serializable persistentUserData) {
            this.persistentUserData = persistentUserData;
            return (T)this;
        }

        @Override
        public T setMetrics(final Metric[] metrics) {
            this.metrics = metrics;
            return (T)this;
        }

        @Override
        public T setReaderCheckpoint(final Serializable reader) {
            this.readerCheckpoint = reader;
            return (T)this;
        }

        @Override
        public T setWriterCheckpoint(final Serializable writer) {
            this.writerCheckpoint = writer;
            return (T)this;
        }
    }
}
