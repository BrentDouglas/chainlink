package io.machinecode.nock.core.impl;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.context.JobContext;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobContextImpl implements JobContext {
    private final JobInstance instance;
    private final JobExecution execution;
    private final Properties properties;
    private Object transientUserData;
    private String exitStatus;

    public JobContextImpl(final JobInstance instance, final JobExecution execution, final Properties properties) {
        this.instance = instance;
        this.execution = execution;
        this.properties = properties;
    }

    @Override
    public String getJobName() {
        return instance.getJobName();
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
        return instance.getInstanceId();
    }

    @Override
    public long getExecutionId() {
        return execution.getExecutionId();
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public BatchStatus getBatchStatus() {
        return execution.getBatchStatus();
    }

    @Override
    public String getExitStatus() {
        return exitStatus;
    }

    @Override
    public void setExitStatus(final String status) {
        this.exitStatus = status;
    }
}
