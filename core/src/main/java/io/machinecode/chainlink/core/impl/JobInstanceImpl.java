package io.machinecode.chainlink.core.impl;

import io.machinecode.chainlink.spi.ExtendedJobInstance;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobInstanceImpl implements ExtendedJobInstance {
    private final long instanceId;
    private final String jobName;
    private final String jslName;

    @Override
    public long getInstanceId() {
        return instanceId;
    }

    @Override
    public String getJobName() {
        return jobName;
    }

    @Override
    public String getJslName() {
        return jslName;
    }

    public JobInstanceImpl(final Builder builder) {
        this.instanceId = builder.instanceId;
        this.jobName = builder.jobName;
        this.jslName = builder.jslName;
    }

    public JobInstanceImpl(final JobInstanceImpl builder) {
        this.instanceId = builder.instanceId;
        this.jobName = builder.jobName;
        this.jslName = builder.jslName;
    }

    public static class Builder {
        private long instanceId;
        private String jobName;
        private String jslName;

        public Builder setInstanceId(final long instanceId) {
            this.instanceId = instanceId;
            return this;
        }

        public Builder setJobName(final String jobName) {
            this.jobName = jobName;
            return this;
        }

        public Builder setJslName(final String jslName) {
            this.jslName = jslName;
            return this;
        }

        public JobInstanceImpl build() {
            return new JobInstanceImpl(this);
        }
    }
}
