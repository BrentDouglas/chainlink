package io.machinecode.chainlink.repository.core;

import io.machinecode.chainlink.spi.repository.ExecutionRepository;

import javax.batch.operations.JobSecurityException;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import java.util.Date;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 * @see https://java.net/bugzilla/show_bug.cgi?id=4834
 */
public class DelegateJobExecution implements JobExecution {
    private final long executionId;
    private final String jobName;
    private final ExecutionRepository repository;

    public DelegateJobExecution(final JobExecution execution, final ExecutionRepository repository) {
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
        try {
            return repository.getJobExecution(executionId).getBatchStatus();
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Date getStartTime() {
        try {
            return repository.getJobExecution(executionId).getStartTime();
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Date getEndTime() {
        try {
            return repository.getJobExecution(executionId).getEndTime();
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getExitStatus() {
        try {
            return repository.getJobExecution(executionId).getExitStatus();
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Date getCreateTime() {
        try {
            return repository.getJobExecution(executionId).getCreateTime();
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Date getLastUpdatedTime() {
        try {
            return repository.getJobExecution(executionId).getLastUpdatedTime();
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Properties getJobParameters() {
        try {
            return repository.getJobExecution(executionId).getJobParameters();
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
