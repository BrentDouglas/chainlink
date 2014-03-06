package io.machinecode.chainlink.repository.core;

import io.machinecode.chainlink.spi.ExtendedJobInstance;

import java.util.ArrayList;
import java.util.List;

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

    public JobInstanceImpl(final ExtendedJobInstance builder) {
        this.instanceId = builder.getInstanceId();
        this.jobName = builder.getJobName();
        this.jslName = builder.getJslName();
    }

    public static List<JobInstanceImpl> copy(final List<? extends ExtendedJobInstance> list) {
        final ArrayList<JobInstanceImpl> copy = new ArrayList<JobInstanceImpl>();
        for (final ExtendedJobInstance item : list) {
            copy.add(new JobInstanceImpl(item));
        }
        return copy;
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
