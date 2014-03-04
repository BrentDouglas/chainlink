package io.machinecode.chainlink.core.impl;

import io.machinecode.chainlink.spi.ExecutionRepository;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import java.util.Date;
import java.util.Properties;

/**
 * @see https://java.net/bugzilla/show_bug.cgi?id=4834
 *
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class DelegateJobExecutionImpl implements JobExecution {
    private final long executionId;
    private final String jobName;
    private final ExecutionRepository repository;

    public DelegateJobExecutionImpl(final JobExecution execution, final ExecutionRepository repository) {
        this.executionId = execution.getExecutionId();
        this.jobName = execution.getJobName();
        this.repository = repository;
    }

    @Override
    public long getExecutionId() {
        return executionId;
    }

    @Override
    public String getJobName() {
        return jobName;
    }

    @Override
    public BatchStatus getBatchStatus() {
        return repository.getJobExecution(executionId).getBatchStatus();
    }

    @Override
    public Date getStartTime() {
        return repository.getJobExecution(executionId).getStartTime();
    }

    @Override
    public Date getEndTime() {
        return repository.getJobExecution(executionId).getEndTime();
    }

    @Override
    public String getExitStatus() {
        return repository.getJobExecution(executionId).getExitStatus();
    }

    @Override
    public Date getCreateTime() {
        return repository.getJobExecution(executionId).getCreateTime();
    }

    @Override
    public Date getLastUpdatedTime() {
        return repository.getJobExecution(executionId).getLastUpdatedTime();
    }

    @Override
    public Properties getJobParameters() {
        return repository.getJobExecution(executionId).getJobParameters();
    }
}
