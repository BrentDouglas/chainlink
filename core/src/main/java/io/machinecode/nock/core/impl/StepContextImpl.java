package io.machinecode.nock.core.impl;

import io.machinecode.nock.spi.context.MutableMetric;
import io.machinecode.nock.spi.context.MutableStepContext;
import io.machinecode.nock.spi.element.execution.Step;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.Metric;
import javax.batch.runtime.Metric.MetricType;
import javax.batch.runtime.StepExecution;
import javax.batch.runtime.context.StepContext;
import java.io.Serializable;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StepContextImpl implements MutableStepContext {
    private final String stepName;
    private final long stepExecutionId;
    private final Properties properties;
    private BatchStatus batchStatus;
    private String exitStatus;
    private String batchletStatus;
    private Object transientUserData;
    private Serializable persistentUserData;
    private Exception exception;
    private MutableMetric[] metrics;

    public StepContextImpl(final long stepExecutionId, final Step<?,?> step, final Properties properties) {
        this.stepExecutionId = stepExecutionId;
        this.stepName = step.getId();
        this.properties = properties;
        this.batchStatus = BatchStatus.STARTING; //TODO ?
        this.exitStatus = null;
        final MetricType[] values = MetricType.values();
        this.metrics = new MutableMetric[values.length];
        for (int i = 0; i < values.length; ++i) {
            this.metrics[i] = new MutableMetricImpl(values[i]);
        }
    }

    public StepContextImpl(final StepContext context) {
        this.stepExecutionId = context.getStepExecutionId();
        this.stepName = context.getStepName();
        this.properties = context.getProperties();
        this.batchStatus = context.getBatchStatus(); //TODO ?
        this.exitStatus = null;
        final MetricType[] values = MetricType.values();
        this.metrics = new MutableMetric[values.length];
        for (int i = 0; i < values.length; ++i) {
            this.metrics[i] = new MutableMetricImpl(values[i]);
        }
    }

    public StepContextImpl(final StepExecution execution, final Properties properties) {
        this.stepExecutionId = execution.getStepExecutionId();
        this.stepName = execution.getStepName();
        this.properties = properties;
        this.batchStatus = execution.getBatchStatus();
        this.exitStatus = null;
        final MetricType[] values = MetricType.values();
        this.metrics = new MutableMetric[values.length];
        for (int i = 0; i < values.length; ++i) {
            this.metrics[i] = new MutableMetricImpl(values[i]);
        }
    }

    @Override
    public String getStepName() {
        return stepName;
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
        return stepExecutionId;
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
    public String getBatchletStatus() {
        return batchletStatus;
    }

    @Override
    public void setBatchletStatus(final String batchletStatus) {
        this.batchletStatus = batchletStatus;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    public void setException(final Exception exception) {
        this.exception = exception;
    }

    @Override
    public Metric[] getMetrics() {
        final MetricImpl[] that = new MetricImpl[metrics.length];
        for (int i = 0; i < metrics.length; ++i) {
            that[i] = new MetricImpl(metrics[i]);
        }
        return that;
    }

    @Override
    public MutableMetric getMetric(final MetricType type) {
        for  (final MutableMetric metric : metrics) {
            if (type == metric.getType()) {
                return metric;
            }
        }
        return null;
    }
}
