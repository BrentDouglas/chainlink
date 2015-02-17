package io.machinecode.chainlink.repository.jpa;

import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Entity
@Table(name = "job_instance")
@NamedQueries({
        @NamedQuery(name = "JpaJobInstance.countWithJobName", query = "select count(i) from JpaJobInstance i where i in (select distinct j from JpaJobInstance j where j.jobName=:jobName)"),
        @NamedQuery(name = "JpaJobInstance.jobNames", query = "select distinct j.jobName from JpaJobInstance j order by j.jobName asc")
})
public class JpaJobInstance implements ExtendedJobInstance, Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String jobName;
    private String jslName;
    private Date createTime;
    private List<JpaJobExecution> jobExecutions;

    public JpaJobInstance() {
    }

    public JpaJobInstance(final JpaJobInstance builder) {
        this.jobName = builder.jobName;
        this.jslName = builder.jslName;
        this.createTime = builder.createTime;
        this.jobExecutions = builder.jobExecutions;
    }

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public long getInstanceId() {
        return id;
    }

    public JpaJobInstance setInstanceId(final long instanceId) {
        this.id = instanceId;
        return this;
    }

    @Override
    @Column(name = "job_name", nullable = false)
    public String getJobName() {
        return jobName;
    }

    public JpaJobInstance setJobName(final String jobName) {
        this.jobName = jobName;
        return this;
    }

    @Override
    @Column(name = "jsl_name", nullable = false)
    public String getJslName() {
        return jslName;
    }

    public JpaJobInstance setJslName(final String jslName) {
        this.jslName = jslName;
        return this;
    }

    @Override
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", length = 29)
    public Date getCreateTime() {
        return createTime;
    }

    public JpaJobInstance setCreateTime(final Date createTime) {
        this.createTime = createTime;
        return this;
    }

    @OneToMany(mappedBy = "jobInstance", cascade = {CascadeType.ALL})
    @OrderBy("createTime desc")
    public List<JpaJobExecution> getJobExecutions() {
        return jobExecutions;
    }

    public JpaJobInstance setJobExecutions(final List<JpaJobExecution> jobExecutions) {
        this.jobExecutions = jobExecutions;
        return this;
    }
}
