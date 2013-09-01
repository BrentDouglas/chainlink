package io.machinecode.nock.core.impl;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.Metric;
import javax.batch.runtime.StepExecution;
import javax.batch.runtime.context.StepContext;
import java.io.Serializable;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StepContextImpl implements StepContext {
    private final StepExecution execution;
    private final Properties properties;
    private String exitStatus;
    private Object transientUserData;
    private Serializable persistentUserData;
    private Exception exception;
    private Metric[] metrics;

    public StepContextImpl(final StepExecution execution, final Properties properties) {
        this.execution = execution;
        this.properties = properties;
    }

    @Override
    public String getStepName() {
        return execution.getStepName();
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
    public long getStepExecutionId() {
        return execution.getStepExecutionId();
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public Serializable getPersistentUserData() {
        return persistentUserData;
    }

    @Override
    public void setPersistentUserData(final Serializable data) {
        this.persistentUserData = data;
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

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    public Metric[] getMetrics() {
        return metrics;
    }
}
