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
package io.machinecode.chainlink.core.context;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.core.repository.MetricImpl;
import io.machinecode.chainlink.core.repository.MutableMetricImpl;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.jsl.execution.Step;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.Metric;
import javax.batch.runtime.Metric.MetricType;
import javax.batch.runtime.context.StepContext;
import java.io.Serializable;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class StepContextImpl implements StepContext, Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(StepContextImpl.class);

    private final String stepName;
    private final long stepExecutionId;
    private final Properties properties;
    private volatile BatchStatus batchStatus;
    private String exitStatus;
    private MutableMetric[] metrics;
    private transient TMap<MetricType, MutableMetric> metricMap = new THashMap<MetricType, MutableMetric>(MetricType.values().length);
    private transient Object transientUserData;
    private Serializable persistentUserData;
    private Exception exception;

    public StepContextImpl(final long stepExecutionId, final Step<?,?> step, final Properties properties) {
        this.stepExecutionId = stepExecutionId;
        this.stepName = step.getId();
        this.properties = properties;
        this.batchStatus = BatchStatus.STARTING;
        this.exitStatus = null;
        final MetricType[] values = MetricType.values();
        this.metrics = new MutableMetric[values.length];
        for (int i = 0; i < values.length; ++i) {
            this.metrics[i] = new MutableMetricImpl(values[i]);
            this.metricMap.put(values[i], this.metrics[i]);
        }
    }

    public StepContextImpl(final StepContext context) {
        this.stepExecutionId = context.getStepExecutionId();
        this.stepName = context.getStepName();
        this.properties = context.getProperties();
        this.batchStatus = BatchStatus.STARTING;
        this.exitStatus = null;
        final MetricType[] values = MetricType.values();
        this.metrics = new MutableMetric[values.length];
        for (int i = 0; i < values.length; ++i) {
            this.metrics[i] = new MutableMetricImpl(values[i]);
            this.metricMap.put(values[i], this.metrics[i]);
        }
    }

    public StepContextImpl(final StepContext context, final Metric[] metrics, final Serializable persistentUserData) {
        this.stepExecutionId = context.getStepExecutionId();
        this.stepName = context.getStepName();
        this.properties = context.getProperties();
        this.batchStatus = BatchStatus.STARTING;
        this.exitStatus = null;
        this.persistentUserData = persistentUserData;
        this.metrics = MutableMetricImpl.copy(metrics);
        for (final MutableMetric metric : this.metrics) {
            this.metricMap.put(metric.getType(), metric);
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

    public void setBatchStatus(final BatchStatus batchStatus) {
        log.debugf(Messages.get("CHAINLINK-028000.step.context.batch.status"), stepExecutionId, stepName, batchStatus);
        this.batchStatus = batchStatus;
    }

    @Override
    public String getExitStatus() {
        return exitStatus;
    }

    @Override
    public void setExitStatus(final String exitStatus) {
        log.debugf(Messages.get("CHAINLINK-028001.step.context.exit.status"), stepExecutionId, stepName, exitStatus);
        this.exitStatus = exitStatus;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    public void setException(final Exception exception) {
        this.exception = exception;
    }

    @Override
    public Metric[] getMetrics() {
        return MetricImpl.copy(metrics);
    }

    public MutableMetric getMetric(final MetricType type) {
        if (this.metricMap == null) {
            this.metricMap = new THashMap<>();
            for (final MutableMetric metric : this.metrics) {
                this.metricMap.put(metric.getType(), metric);
            }
        }
        return this.metricMap.get(type);
    }
}
