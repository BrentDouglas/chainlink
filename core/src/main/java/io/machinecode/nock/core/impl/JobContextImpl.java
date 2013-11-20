package io.machinecode.nock.core.impl;

import io.machinecode.nock.spi.context.MutableJobContext;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.context.JobContext;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobContextImpl implements MutableJobContext {
    private final long instanceId;
    private final String jobName;
    private final long executionId;
    private final Properties properties;
    private Object transientUserData;
    private BatchStatus batchStatus;
    private String exitStatus;

    public JobContextImpl(final JobContext context) {
        this.instanceId = context.getInstanceId();
        this.jobName = context.getJobName();
        this.executionId = context.getExecutionId();
        this.properties = context.getProperties();
        this.batchStatus = context.getBatchStatus(); //TODO ?
        this.exitStatus = context.getExitStatus();
    }

    public JobContextImpl(final JobInstance instance, final JobExecution execution, final Properties properties) {
        this.instanceId = instance.getInstanceId();
        this.jobName = instance.getJobName();
        this.executionId = execution.getExecutionId();
        this.properties = properties;
        this.batchStatus = execution.getBatchStatus();
        this.exitStatus = execution.getExitStatus();
    }

    @Override
    public String getJobName() {
        return jobName;
    }

    @Override
    public Object getTransientUserData() {
        return transientUserData;
    }

    @Override
    public void setTransientUserData(final Object data) {
        this.transientUserData = data;
    }

    @Override
    public long getInstanceId() {
        return instanceId;
    }

    @Override
    public long getExecutionId() {
        return executionId;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public BatchStatus getBatchStatus() {
        return batchStatus;
    }

    @Override
    public void setBatchStatus(final BatchStatus batchStatus) {
        this.batchStatus = batchStatus;
    }

    @Override
    public String getExitStatus() {
        return exitStatus;
    }

    @Override
    public void setExitStatus(final String status) {
        this.exitStatus = status;
    }

    @Override
    public JobContextImpl copy() {
        return new JobContextImpl(this);
    }
}
