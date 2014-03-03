package io.machinecode.nock.core.impl;

import io.machinecode.nock.spi.ExecutionRepository;
import io.machinecode.nock.spi.context.MutableMetric;
import io.machinecode.nock.spi.context.MutableStepContext;
import io.machinecode.nock.spi.element.execution.Step;
import io.machinecode.nock.spi.util.Messages;
import org.jboss.logging.Logger;

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

    private static final Logger log = Logger.getLogger(StepContextImpl.class);

    private final String stepName;
    private final long stepExecutionId;
    private final Properties properties;
    private volatile BatchStatus batchStatus;
    private String exitStatus;
    private MutableMetric[] metrics;
    private Object transientUserData;
    private Serializable persistentUserData;
    private Exception exception;

    public StepContextImpl(final String stepName, final long stepExecutionId, final Properties properties, final BatchStatus batchStatus,
                           final String exitStatus, final MutableMetric[] metrics, final Serializable persistentUserData) {
        this.stepName = stepName;
        this.stepExecutionId = stepExecutionId;
        this.properties = properties;
        this.batchStatus = batchStatus;
        this.exitStatus = exitStatus;
        this.metrics = metrics;
        this.persistentUserData = persistentUserData;
    }

    public StepContextImpl(final long stepExecutionId, final Step<?,?> step, final Properties properties, final ExecutionRepository repository) {
        this.stepExecutionId = stepExecutionId;
        this.stepName = step.getId();
        this.properties = properties;
        this.batchStatus = BatchStatus.STARTING;
        this.exitStatus = null;
        final MetricType[] values = MetricType.values();
        this.metrics = new MutableMetric[values.length];
        for (int i = 0; i < values.length; ++i) {
            this.metrics[i] = repository.createMetric(values[i]);
        }
    }

    public StepContextImpl(final StepContext context, final ExecutionRepository repository) {
        this.stepExecutionId = context.getStepExecutionId();
        this.stepName = context.getStepName();
        this.properties = context.getProperties();
        this.batchStatus = BatchStatus.STARTING;
        this.exitStatus = null;
        final MetricType[] values = MetricType.values();
        this.metrics = new MutableMetric[values.length];
        for (int i = 0; i < values.length; ++i) {
            this.metrics[i] = repository.createMetric(values[i]);
        }
    }

    public StepContextImpl(final StepContext context, final Metric[] metrics, final Serializable persistentUserData, final ExecutionRepository repository) {
        this.stepExecutionId = context.getStepExecutionId();
        this.stepName = context.getStepName();
        this.properties = context.getProperties();
        this.batchStatus = BatchStatus.STARTING;
        this.exitStatus = null;
        this.persistentUserData = persistentUserData;
        this.metrics = repository.copyMetrics(metrics);
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
        log.debugf(Messages.get("NOCK-028000.step.context.batch.status"), stepExecutionId, stepName, batchStatus);
        this.batchStatus = batchStatus;
    }

    @Override
    public String getExitStatus() {
        return exitStatus;
    }

    @Override
    public void setExitStatus(final String exitStatus) {
        log.debugf(Messages.get("NOCK-028001.step.context.exit.status"), stepExecutionId, stepName, exitStatus);
        this.exitStatus = exitStatus;
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

    @Override
    public MutableMetric[] getMutableMetrics() {
        return metrics; //TODO Copy instead?
    }

    @Override
    public void setFrom(final MutableStepContext stepContext) {
        this.batchStatus = stepContext.getBatchStatus();
        this.exitStatus = stepContext.getExitStatus();
        this.transientUserData = stepContext.getTransientUserData();
        this.persistentUserData = stepContext.getPersistentUserData();
        this.exception = stepContext.getException();
        this.metrics = stepContext.getMutableMetrics();
    }
}
