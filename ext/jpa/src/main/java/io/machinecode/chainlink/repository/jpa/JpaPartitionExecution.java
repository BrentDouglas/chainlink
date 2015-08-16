/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package io.machinecode.chainlink.repository.jpa;

import io.machinecode.chainlink.spi.repository.PartitionExecution;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.Metric;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Entity
@Table(name = "partition_execution")
@NamedQueries({
        @NamedQuery(name = "JpaPartitionExecution.unfinishedForStepExecutionId", query = "select p from JpaPartitionExecution p where p.stepExecution.id=:stepExecutionId and p.batchStatus in (javax.batch.runtime.BatchStatus.FAILED, javax.batch.runtime.BatchStatus.STOPPED, javax.batch.runtime.BatchStatus.STOPPING, javax.batch.runtime.BatchStatus.STARTED, javax.batch.runtime.BatchStatus.STARTING)")
})
public class JpaPartitionExecution implements PartitionExecution, Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private JpaStepExecution stepExecution;
    private int partitionId;
    private BatchStatus batchStatus;
    private String exitStatus;
    private Date createTime;
    private Date startTime;
    private Date updatedTime;
    private Date endTime;
    private Serializable persistentUserData;
    private Serializable readerCheckpoint;
    private Serializable writerCheckpoint;
    private Map<Metric.MetricType, JpaMetric> metrics;
    private List<JpaProperty> parameters;

    public JpaPartitionExecution() {
    }

    public JpaPartitionExecution(final JpaPartitionExecution builder) {
        this.stepExecution = builder.stepExecution;
        this.partitionId = builder.partitionId;
        this.batchStatus = builder.batchStatus;
        this.exitStatus = builder.exitStatus;
        this.createTime = builder.createTime;
        this.startTime = builder.startTime;
        this.updatedTime = builder.updatedTime;
        this.endTime = builder.endTime;
        this.persistentUserData = builder.persistentUserData;
        this.metrics = JpaMetric.copy(builder.metrics);
        this.readerCheckpoint = builder.readerCheckpoint;
        this.writerCheckpoint = builder.writerCheckpoint;
        this.parameters = JpaProperty.copy(builder.parameters);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    @Override
    @Transient
    public long getPartitionExecutionId() {
        return id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_execution_id")
    public JpaStepExecution getStepExecution() {
        return stepExecution;
    }

    public JpaPartitionExecution setStepExecution(final JpaStepExecution stepExecution) {
        this.stepExecution = stepExecution;
        return this;
    }

    @Override
    @Transient
    public long getStepExecutionId() {
        return stepExecution.getStepExecutionId();
    }

    @Override
    @Column(name = "partition_id", nullable = false)
    public int getPartitionId() {
        return partitionId;
    }

    public JpaPartitionExecution setPartitionId(final int partitionId) {
        this.partitionId = partitionId;
        return this;
    }

    @Override
    @Transient
    public Properties getPartitionParameters() {
        if (this.parameters == null) {
            return null;
        }
        final Properties params = new Properties();
        for (final JpaProperty property : parameters) {
            params.put(property.getName(), property.getValue());
        }
        return params;
    }

    public JpaPartitionExecution setPartitionParameters(final Properties parameters) {
        if (parameters == null) {
            this.parameters = null;
            return this;
        }
        final ArrayList<JpaProperty> params = new ArrayList<JpaProperty>();
        for (final String key : parameters.stringPropertyNames()) {
            params.add(new JpaProperty(key, parameters.getProperty(key)));
        }
        this.parameters = params;
        return this;
    }

    @Override
    @Enumerated(EnumType.STRING)
    @Column(name = "batch_status", nullable = false, length = 9)
    public BatchStatus getBatchStatus() {
        return batchStatus;
    }

    public JpaPartitionExecution setBatchStatus(final BatchStatus batchStatus) {
        this.batchStatus = batchStatus;
        return this;
    }

    @Override
    @Column(name = "exit_status")
    public String getExitStatus() {
        return exitStatus;
    }

    public JpaPartitionExecution setExitStatus(final String exitStatus) {
        this.exitStatus = exitStatus;
        return this;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", nullable = false, length = 29)
    public Date getCreateTime() {
        return createTime;
    }

    public JpaPartitionExecution setCreateTime(final Date createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_time", length = 29)
    public Date getStartTime() {
        return startTime;
    }

    public JpaPartitionExecution setStartTime(final Date startTime) {
        this.startTime = startTime;
        return this;
    }

    @Override
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_time", length = 29)
    public Date getUpdatedTime() {
        return updatedTime;
    }

    public JpaPartitionExecution setUpdatedTime(final Date updatedTime) {
        this.updatedTime = updatedTime;
        return this;
    }

    @Override
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_time", length = 29)
    public Date getEndTime() {
        return endTime;
    }

    public JpaPartitionExecution setEndTime(final Date endTime) {
        this.endTime = endTime;
        return this;
    }

    @Override
    @Lob
    @Column(name = "persistent_user_data")
    public Serializable getPersistentUserData() {
        return persistentUserData;
    }

    public JpaPartitionExecution setPersistentUserData(final Serializable persistentUserData) {
        this.persistentUserData = persistentUserData;
        return this;
    }

    @Override
    @Lob
    @Column(name = "reader_checkpoint")
    public Serializable getReaderCheckpoint() {
        return readerCheckpoint;
    }

    public JpaPartitionExecution setReaderCheckpoint(final Serializable reader) {
        this.readerCheckpoint = reader;
        return this;
    }

    @Override
    @Lob
    @Column(name = "writer_checkpoint")
    public Serializable getWriterCheckpoint() {
        return writerCheckpoint;
    }

    public JpaPartitionExecution setWriterCheckpoint(final Serializable writer) {
        this.writerCheckpoint = writer;
        return this;
    }

    @Override
    @Transient
    public JpaMetric[] getMetrics() {
        return metrics == null ? null : metrics.values().toArray(new JpaMetric[metrics.size()]);
    }

    @OrderColumn(name = "id")
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "metric_type", nullable = false)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "partition_execution_metric",
            joinColumns = {@JoinColumn(name = "partition_execution_id")},
            inverseJoinColumns = {@JoinColumn(name = "metric_id")
    })
    public Map<Metric.MetricType, JpaMetric> getMetricsMap() {
        return metrics;
    }

    public JpaPartitionExecution setMetricsMap(final Map<Metric.MetricType, JpaMetric> metrics) {
        this.metrics = metrics;
        return this;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "partition_execution_property",
            joinColumns = {@JoinColumn(name = "partition_execution_id")},
            inverseJoinColumns = {@JoinColumn(name = "property_id")
    })
    public List<JpaProperty> getParameters() {
        return parameters;
    }

    public JpaPartitionExecution setParameters(final List<JpaProperty> parameters) {
        this.parameters = parameters;
        return this;
    }
}