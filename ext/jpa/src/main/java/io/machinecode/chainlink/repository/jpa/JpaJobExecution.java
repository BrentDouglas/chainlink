package io.machinecode.chainlink.repository.jpa;

import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;

import javax.batch.runtime.BatchStatus;
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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Entity
@Table(name = "job_execution")
@NamedQueries({
        @NamedQuery(name = "JpaJobExecution.byCreateDate", query = "select j from JpaJobExecution j where j.jobInstance.id=:jobInstanceId order by j.createTime desc"),
        @NamedQuery(name = "JpaJobExecution.runningJobExecutionIds", query = "select j.id from JpaJobExecution j where j.jobName=:jobName and j.batchStatus='STARTED' order by j.createTime desc"),
        @NamedQuery(name = "JpaJobExecution.withJobName", query = "select j from JpaJobInstance j where j.jobName=:jobName order by j.createTime asc"),
        @NamedQuery(name = "JpaJobExecution.countWithJobName", query = "select count(j) from JpaJobInstance j where j.jobName=:jobName"),
        @NamedQuery(name = "JpaJobExecution.previous", query = "select h.previousJobExecution from JpaJobExecutionHistory h where h.jobExecution.id=:jobExecutionId")
})
public class JpaJobExecution implements ExtendedJobExecution, Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private JpaJobInstance jobInstance;
    private String jobName;
    private BatchStatus batchStatus;
    private String exitStatus;
    private Date startTime;
    private Date endTime;
    private Date createTime;
    private Date updatedTime;
    private String restartElementId;
    private List<JpaProperty> parameters;
    private List<JpaStepExecution> stepExecutions;

    public JpaJobExecution() {
    }

    public JpaJobExecution(final JpaJobExecution builder) {
        this.jobName = builder.jobName;
        this.batchStatus = builder.batchStatus;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.exitStatus = builder.exitStatus;
        this.createTime = builder.createTime;
        this.updatedTime = builder.updatedTime;
        this.parameters = JpaProperty.copy(builder.parameters);
        this.restartElementId = builder.restartElementId;
        this.jobInstance = builder.jobInstance;
        this.stepExecutions = builder.stepExecutions;
    }

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public long getExecutionId() {
        return id;
    }

    public JpaJobExecution setExecutionId(final long executionId) {
        this.id = executionId;
        return this;
    }

    @Override
    @Column(name = "job_name", nullable = false)
    public String getJobName() {
        return jobName;
    }

    public JpaJobExecution setJobName(final String jobName) {
        this.jobName = jobName;
        return this;
    }

    @Override
    @Enumerated(EnumType.STRING)
    @Column(name = "batch_status", nullable = false, length = 9)
    public BatchStatus getBatchStatus() {
        return batchStatus;
    }

    public JpaJobExecution setBatchStatus(final BatchStatus batchStatus) {
        this.batchStatus = batchStatus;
        return this;
    }

    @Override
    @Column(name = "exit_status")
    public String getExitStatus() {
        return exitStatus;
    }

    public JpaJobExecution setExitStatus(final String exitStatus) {
        this.exitStatus = exitStatus;
        return this;
    }

    @Override
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", nullable = false, length = 29)
    public Date getCreateTime() {
        return createTime;
    }

    public JpaJobExecution setCreateTime(final Date createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_time", length = 29)
    public Date getStartTime() {
        return startTime;
    }

    public JpaJobExecution setStartTime(final Date startTime) {
        this.startTime = startTime;
        return this;
    }

    @Override
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_time", length = 29)
    public Date getLastUpdatedTime() {
        return updatedTime;
    }

    public JpaJobExecution setLastUpdatedTime(final Date updatedTime) {
        this.updatedTime = updatedTime;
        return this;
    }

    @Override
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_time", length = 29)
    public Date getEndTime() {
        return endTime;
    }

    public JpaJobExecution setEndTime(final Date endTime) {
        this.endTime = endTime;
        return this;
    }

    @Override
    @Transient
    public Properties getJobParameters() {
        if (this.parameters == null) {
            return null;
        }
        final Properties params = new Properties();
        for (final JpaProperty property : parameters) {
            params.put(property.getName(), property.getValue());
        }
        return params;
    }

    public JpaJobExecution setJobParameters(final Properties parameters) {
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

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "job_execution_property",
            joinColumns = {@JoinColumn(name = "job_execution_id")},
            inverseJoinColumns = {@JoinColumn(name = "property_id")
    })
    public List<JpaProperty> getParameters() {
        return parameters;
    }

    public JpaJobExecution setParameters(final List<JpaProperty> parameters) {
        this.parameters = parameters;
        return this;
    }

    @Override
    @Column(name = "restart_element_id")
    public String getRestartElementId() {
        return restartElementId;
    }

    @Override
    @Transient
    public long getJobInstanceId() {
        return jobInstance.getInstanceId();
    }

    public JpaJobExecution setRestartElementId(final String restartElementId) {
        this.restartElementId = restartElementId;
        return this;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_instance_id", nullable = false)
    public JpaJobInstance getJobInstance() {
        return jobInstance;
    }

    public JpaJobExecution setJobInstance(final JpaJobInstance jobInstance) {
        this.jobInstance = jobInstance;
        return this;
    }

    @OneToMany(mappedBy = "jobExecution", cascade = {CascadeType.ALL})
    @OrderBy("startTime desc")
    public List<JpaStepExecution> getStepExecutions() {
        return stepExecutions;
    }

    public JpaJobExecution setStepExecutions(final List<JpaStepExecution> stepExecutions) {
        this.stepExecutions = stepExecutions;
        return this;
    }

    @Override
    public String toString() {
        return "JpaJobExecution{" +
                "id=" + id +
                ", jobInstance=" + jobInstance +
                ", jobName='" + jobName + '\'' +
                ", batchStatus=" + batchStatus +
                ", exitStatus='" + exitStatus + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", createTime=" + createTime +
                ", updatedTime=" + updatedTime +
                ", restartElementId='" + restartElementId + '\'' +
                ", parameters=" + parameters +
                ", stepExecutions=" + stepExecutions +
                '}';
    }
}
