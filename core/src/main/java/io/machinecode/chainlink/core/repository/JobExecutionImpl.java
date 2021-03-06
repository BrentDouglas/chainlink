/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.core.repository;

import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JobExecutionImpl implements ExtendedJobExecution, Serializable {
    private static final long serialVersionUID = 1L;

    private final long jobInstanceId;
    private final long jobExecutionId;
    private final String jobName;
    private final BatchStatus batchStatus;
    private final Date startTime;
    private final Date endTime;
    private final String exitStatus;
    private final Date createTime;
    private final Date lastUpdatedTime;
    private final Properties jobParameters;
    private final String restartElementId;

    public JobExecutionImpl(final _Builder builder) {
        this.jobInstanceId = builder.jobInstanceId;
        this.jobExecutionId = builder.jobExecutionId;
        this.jobName = builder.jobName;
        this.batchStatus = builder.batchStatus;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.exitStatus = builder.exitStatus;
        this.createTime = builder.createTime;
        this.lastUpdatedTime = builder.lastUpdatedTime;
        this.jobParameters = builder.jobParameters;
        this.restartElementId = builder.restartElementId;
    }

    public JobExecutionImpl(final ExtendedJobExecution builder) {
        this.jobInstanceId = builder.getJobInstanceId();
        this.jobExecutionId = builder.getExecutionId();
        this.jobName = builder.getJobName();
        this.batchStatus = builder.getBatchStatus();
        this.startTime = builder.getStartTime();
        this.endTime = builder.getEndTime();
        this.exitStatus = builder.getExitStatus();
        this.createTime = builder.getCreateTime();
        this.lastUpdatedTime = builder.getLastUpdatedTime();
        this.jobParameters = builder.getJobParameters();
        this.restartElementId = builder.getRestartElementId();
    }

    public static List<JobExecutionImpl> copy(final List<? extends ExtendedJobExecution> list) {
        final ArrayList<JobExecutionImpl> copy = new ArrayList<JobExecutionImpl>();
        for (final ExtendedJobExecution item : list) {
            copy.add(new JobExecutionImpl(item));
        }
        return copy;
    }

    @Override
    public long getJobInstanceId() {
        return jobInstanceId;
    }

    @Override
    public long getExecutionId() {
        return jobExecutionId;
    }

    @Override
    public String getJobName() {
        return jobName;
    }

    @Override
    public BatchStatus getBatchStatus() {
        return batchStatus;
    }

    @Override
    public Date getStartTime() {
        return startTime;
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
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    @Override
    public Properties getJobParameters() {
        return jobParameters;
    }

    @Override
    public String getRestartElementId() {
        return restartElementId;
    }

    public static Builder from(final ExtendedJobExecution that) {
        final Builder builder = new Builder();
        JobExecutionImpl._from(builder, that);
        return builder;
    }

    protected static void _from(final _Builder<?> builder, final ExtendedJobExecution that) {
        builder.jobInstanceId = that.getJobInstanceId();
        builder.jobExecutionId = that.getExecutionId();
        builder.jobName = that.getJobName();
        builder.batchStatus = that.getBatchStatus();
        builder.startTime = that.getStartTime();
        builder.endTime = that.getEndTime();
        builder.exitStatus = that.getExitStatus();
        builder.createTime = that.getCreateTime();
        builder.lastUpdatedTime = that.getLastUpdatedTime();
        builder.jobParameters = that.getJobParameters();
        builder.restartElementId = that.getRestartElementId();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append("{");
        _toString(sb);
        sb.append('}');
        return sb.toString();
    }

    protected void _toString(final StringBuilder sb) {
        sb.append("jobInstanceId=").append(jobInstanceId);
        sb.append(", jobExecutionId=").append(jobExecutionId);
        sb.append(", jobName='").append(jobName).append('\'');
        sb.append(", batchStatus=").append(batchStatus);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", exitStatus='").append(exitStatus).append('\'');
        sb.append(", createTime=").append(createTime);
        sb.append(", lastUpdatedTime=").append(lastUpdatedTime);
        sb.append(", jobParameters=").append(jobParameters);
        sb.append(", restartElementId='").append(restartElementId).append('\'');
    }

    @SuppressWarnings("unchecked")
    public abstract static class _Builder<T extends _Builder<T>> implements ExtendedJobExecutionBuilder<T> {
        long jobInstanceId;
        long jobExecutionId;
        String jobName;
        BatchStatus batchStatus;
        Date startTime;
        Date endTime;
        String exitStatus;
        Date createTime;
        Date lastUpdatedTime;
        Properties jobParameters;
        String restartElementId;

        @Override
        public T setJobInstanceId(final long jobInstanceId) {
            this.jobInstanceId = jobInstanceId;
            return (T)this;
        }

        @Override
        public T setJobExecutionId(final long jobExecutionId) {
            this.jobExecutionId = jobExecutionId;
            return (T)this;
        }

        @Override
        public T setJobName(final String jobName) {
            this.jobName = jobName;
            return (T)this;
        }

        @Override
        public T setBatchStatus(final BatchStatus batchStatus) {
            this.batchStatus = batchStatus;
            return (T)this;
        }

        @Override
        public T setStartTime(final Date startTime) {
            this.startTime = startTime;
            return (T)this;
        }

        @Override
        public T setEndTime(final Date endTime) {
            this.endTime = endTime;
            return (T)this;
        }

        @Override
        public T setExitStatus(final String exitStatus) {
            this.exitStatus = exitStatus;
            return (T)this;
        }

        @Override
        public T setCreateTime(final Date createTime) {
            this.createTime = createTime;
            return (T)this;
        }

        @Override
        public T setLastUpdatedTime(final Date lastUpdatedTime) {
            this.lastUpdatedTime = lastUpdatedTime;
            return (T)this;
        }

        @Override
        public T setJobParameters(final Properties jobParameters) {
            this.jobParameters = jobParameters;
            return (T)this;
        }

        @Override
        public T setRestartElementId(final String restartElementId) {
            this.restartElementId = restartElementId;
            return (T)this;
        }

        public abstract ExtendedJobExecution build();
    }

    public static class Builder extends _Builder<Builder> {
        @Override
        public JobExecutionImpl build() {
            return new JobExecutionImpl(this);
        }
    }
}
