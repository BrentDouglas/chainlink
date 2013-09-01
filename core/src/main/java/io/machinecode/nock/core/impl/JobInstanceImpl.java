package io.machinecode.nock.core.impl;

import javax.batch.runtime.JobInstance;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobInstanceImpl implements JobInstance {
    private final long instanceId;
    private final String jobName;

    @Override
    public long getInstanceId() {
        return instanceId;
    }

    @Override
    public String getJobName() {
        return jobName;
    }

    public JobInstanceImpl(final Builder builder) {
        this.instanceId = builder.instanceId;
        this.jobName = builder.jobName;
    }

    public JobInstanceImpl(final JobInstanceImpl builder) {
        this.instanceId = builder.instanceId;
        this.jobName = builder.jobName;
    }

    public static class Builder {
        private long instanceId;
        private String jobName;

        public Builder setInstanceId(final long instanceId) {
            this.instanceId = instanceId;
            return this;
        }

        public Builder setJobName(final String jobName) {
            this.jobName = jobName;
            return this;
        }

        public JobInstanceImpl build() {
            return new JobInstanceImpl(this);
        }
    }
}
