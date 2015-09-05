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

import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JobInstanceImpl implements ExtendedJobInstance, Serializable {
    private static final long serialVersionUID = 1L;

    private final long jobInstanceId;
    private final String jobName;
    private final String jslName;
    private final Date createTime;

    @Override
    public long getInstanceId() {
        return jobInstanceId;
    }

    @Override
    public String getJobName() {
        return jobName;
    }

    @Override
    public String getJslName() {
        return jslName;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
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
        sb.append(", jobName='").append(jobName).append('\'');
        sb.append(", jslName='").append(jslName).append('\'');
        sb.append(", createTime=").append(createTime);
    }

    public JobInstanceImpl(final _Builder builder) {
        this.jobInstanceId = builder.jobInstanceId;
        this.jobName = builder.jobName;
        this.jslName = builder.jslName;
        this.createTime = builder.createTime;
    }

    public JobInstanceImpl(final ExtendedJobInstance builder) {
        this.jobInstanceId = builder.getInstanceId();
        this.jobName = builder.getJobName();
        this.jslName = builder.getJslName();
        this.createTime = builder.getCreateTime();
    }

    public static List<JobInstanceImpl> copy(final List<? extends ExtendedJobInstance> list) {
        final ArrayList<JobInstanceImpl> copy = new ArrayList<JobInstanceImpl>();
        for (final ExtendedJobInstance item : list) {
            copy.add(new JobInstanceImpl(item));
        }
        return copy;
    }

    @SuppressWarnings("unchecked")
    public abstract static class _Builder<T extends _Builder<T>> implements ExtendedJobInstanceBuilder<T> {
        long jobInstanceId;
        String jobName;
        String jslName;
        Date createTime;

        @Override
        public T setJobInstanceId(final long jobInstanceId) {
            this.jobInstanceId = jobInstanceId;
            return (T)this;
        }

        @Override
        public T setJobName(final String jobName) {
            this.jobName = jobName;
            return (T)this;
        }

        @Override
        public T setJslName(final String jslName) {
            this.jslName = jslName;
            return (T)this;
        }

        @Override
        public T setCreateTime(final Date createTime) {
            this.createTime = createTime;
            return (T)this;
        }

        public abstract ExtendedJobInstance build();
    }

    public static class Builder extends _Builder<Builder> {
        @Override
        public JobInstanceImpl build() {
            return new JobInstanceImpl(this);
        }
    }
}
