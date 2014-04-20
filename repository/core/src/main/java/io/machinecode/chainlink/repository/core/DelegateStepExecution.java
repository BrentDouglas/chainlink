package io.machinecode.chainlink.repository.core;

import io.machinecode.chainlink.spi.repository.ExecutionRepository;

import javax.batch.operations.JobSecurityException;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.Metric;
import javax.batch.runtime.StepExecution;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 * @see https://java.net/bugzilla/show_bug.cgi?id=4834
 */
public class DelegateStepExecution implements StepExecution {
    private final long executionId;
    private final String stepName;
    private final ExecutionRepository repository;

    public DelegateStepExecution(final StepExecution execution, final ExecutionRepository repository) {
        this.executionId = execution.getStepExecutionId();
        this.stepName = execution.getStepName();
        this.repository = repository;
    }

    @Override
    public long getStepExecutionId() {
        return executionId;
    }

    @Override
    public String getStepName() {
        return stepName;
    }

    @Override
    public BatchStatus getBatchStatus() {
        try {
            return repository.getStepExecution(executionId).getBatchStatus();
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
            return repository.getStepExecution(executionId).getStartTime();
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
            return repository.getStepExecution(executionId).getEndTime();
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
            return repository.getStepExecution(executionId).getExitStatus();
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Serializable getPersistentUserData() {
        try {
            return repository.getStepExecution(executionId).getPersistentUserData();
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Metric[] getMetrics() {
        try {
            return repository.getStepExecution(executionId).getMetrics();
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
