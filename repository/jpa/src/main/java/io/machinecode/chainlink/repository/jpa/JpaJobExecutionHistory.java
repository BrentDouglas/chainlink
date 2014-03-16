package io.machinecode.chainlink.repository.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@Entity
@Table(name = "job_execution_history", schema = "public")
public class JpaJobExecutionHistory {
    private long id;
    private JpaJobExecution jobExecution;
    private JpaJobExecution previousJobExecution;

    public JpaJobExecutionHistory() {
    }

    public JpaJobExecutionHistory(final JpaJobExecutionHistory builder) {
        this.jobExecution = builder.jobExecution;
        this.previousJobExecution = builder.previousJobExecution;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public long getId() {
        return id;
    }

    public JpaJobExecutionHistory setId(final long id) {
        this.id = id;
        return this;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_execution_id")
    public JpaJobExecution getJobExecution() {
        return jobExecution;
    }

    public JpaJobExecutionHistory setJobExecution(final JpaJobExecution jobExecution) {
        this.jobExecution = jobExecution;
        return this;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_job_execution_id")
    public JpaJobExecution getPreviousJobExecution() {
        return previousJobExecution;
    }

    public JpaJobExecutionHistory setPreviousJobExecution(final JpaJobExecution previousJobExecution) {
        this.previousJobExecution = previousJobExecution;
        return this;
    }
}
