package io.machinecode.chainlink.repository.jpa;

import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;

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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@Entity
@Table(name = "step_execution")
@NamedQueries({
        @NamedQuery(name = "JpaStepExecution.countWithJobExecutionIdAndStepName", query = "select count(s) from JpaStepExecution s join s.jobExecution j where j in (select p from JpaJobExecutionHistory h join h.previousJobExecution p where h.jobExecution.id=:jobExecutionId) and s.stepName=:stepName"),
        @NamedQuery(name = "JpaStepExecution.withJobExecutionIdAndStepName", query = "select s from JpaStepExecution s join s.jobExecution j where (j.id=:jobExecutionId or j in (select p from JpaJobExecutionHistory h join h.previousJobExecution p where h.jobExecution.id=:jobExecutionId)) and s.stepName=:stepName"),
        @NamedQuery(name = "JpaStepExecution.previous", query = "select s from JpaStepExecution s join s.jobExecution j where (j.id=:jobExecutionId or j in (select p from JpaJobExecutionHistory h join h.previousJobExecution p where h.jobExecution.id=:jobExecutionId)) and s.id!=:stepExecutionId and s.createTime < (select t.createTime from JpaStepExecution t where t.id = :stepExecutionId) and s.stepName =:stepName order by s.createTime desc"),
        @NamedQuery(name = "JpaStepExecution.latest", query = "select s from JpaStepExecution s join s.jobExecution j where (j.id=:jobExecutionId or j in (select p from JpaJobExecutionHistory h join h.previousJobExecution p where h.jobExecution.id=:jobExecutionId)) and s.stepName=:stepName order by s.createTime desc")
})
public class JpaStepExecution implements ExtendedStepExecution {
    private long id;
    private String stepName;
    private JpaJobExecution jobExecution;
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
    private List<JpaPartitionExecution> partitionExecutions;

    public JpaStepExecution() {
    }

    public JpaStepExecution(final JpaStepExecution builder) {
        this.stepName = builder.stepName;
        this.jobExecution = builder.jobExecution;
        this.batchStatus = builder.batchStatus;
        this.exitStatus = builder.exitStatus;
        this.createTime = builder.createTime;
        this.startTime = builder.startTime;
        this.updatedTime = builder.updatedTime;
        this.endTime = builder.endTime;
        this.persistentUserData = builder.persistentUserData;
        this.readerCheckpoint = builder.readerCheckpoint;
        this.writerCheckpoint = builder.writerCheckpoint;
        this.metrics = JpaMetric.copy(builder.metrics);
        this.partitionExecutions = builder.partitionExecutions;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public long getId() {
        return id;
    }

    public JpaStepExecution setId(final long id) {
        this.id = id;
        return this;
    }

    @Override
    @Transient
    public long getStepExecutionId() {
        return id;
    }

    @Override
    @Column(name = "step_name", nullable = false)
    public String getStepName() {
        return stepName;
    }

    public JpaStepExecution setStepName(final String stepName) {
        this.stepName = stepName;
        return this;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_execution_id")
    public JpaJobExecution getJobExecution() {
        return jobExecution;
    }

    public JpaStepExecution setJobExecution(final JpaJobExecution jobExecution) {
        this.jobExecution = jobExecution;
        return this;
    }

    @Override
    @Enumerated(EnumType.STRING)
    @Column(name = "batch_status", nullable = false, length = 9)
    public BatchStatus getBatchStatus() {
        return batchStatus;
    }

    public JpaStepExecution setBatchStatus(final BatchStatus batchStatus) {
        this.batchStatus = batchStatus;
        return this;
    }

    @Override
    @Column(name = "exit_status")
    public String getExitStatus() {
        return exitStatus;
    }

    public JpaStepExecution setExitStatus(final String exitStatus) {
        this.exitStatus = exitStatus;
        return this;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", nullable = false, length = 29)
    public Date getCreateTime() {
        return createTime;
    }

    public JpaStepExecution setCreateTime(final Date createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_time", length = 29)
    public Date getStartTime() {
        return startTime;
    }

    public JpaStepExecution setStartTime(final Date start) {
        this.startTime = start;
        return this;
    }

    @Override
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_time", length = 29)
    public Date getUpdatedTime() {
        return updatedTime;
    }

    public JpaStepExecution setUpdatedTime(final Date updated) {
        this.updatedTime = updated;
        return this;
    }

    @Override
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_time", length = 29)
    public Date getEndTime() {
        return endTime;
    }

    public JpaStepExecution setEndTime(final Date end) {
        this.endTime = end;
        return this;
    }

    @Override
    @Lob
    @Column(name = "persistent_user_data")
    public Serializable getPersistentUserData() {
        return persistentUserData;
    }

    public JpaStepExecution setPersistentUserData(final Serializable persistentUserData) {
        this.persistentUserData = persistentUserData;
        return this;
    }

    @Override
    @Lob
    @Column(name = "reader_checkpoint")
    public Serializable getReaderCheckpoint() {
        return readerCheckpoint;
    }

    public JpaStepExecution setReaderCheckpoint(final Serializable reader) {
        this.readerCheckpoint = reader;
        return this;
    }

    @Override
    @Lob
    @Column(name = "writer_checkpoint")
    public Serializable getWriterCheckpoint() {
        return writerCheckpoint;
    }

    public JpaStepExecution setWriterCheckpoint(final Serializable writer) {
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
    @JoinTable(name = "step_execution_metric",
            joinColumns = {@JoinColumn(name = "step_execution_id")},
            inverseJoinColumns = {@JoinColumn(name = "metric_id")
    })
    public Map<Metric.MetricType, JpaMetric> getMetricsMap() {
        return metrics;
    }

    public JpaStepExecution setMetricsMap(final Map<Metric.MetricType, JpaMetric> metrics) {
        this.metrics = metrics;
        return this;
    }

    @OneToMany(mappedBy = "stepExecution", cascade = {CascadeType.ALL})
    @OrderBy("startTime desc")
    public List<JpaPartitionExecution> getPartitionExecutions() {
        return partitionExecutions;
    }

    public JpaStepExecution setPartitionExecutions(final List<JpaPartitionExecution> partitionExecutions) {
        this.partitionExecutions = partitionExecutions;
        return this;
    }

    @Override
    @Transient
    public long getJobExecutionId() {
        return jobExecution.getExecutionId();
    }
}
