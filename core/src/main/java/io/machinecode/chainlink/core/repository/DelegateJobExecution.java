package io.machinecode.chainlink.core.repository;

import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.Repository;

import javax.batch.operations.BatchRuntimeException;
import javax.batch.operations.JobSecurityException;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import java.util.Date;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @see https://java.net/bugzilla/show_bug.cgi?id=4834
 */
public class DelegateJobExecution implements ExtendedJobExecution {
    private final long executionId;
    private final String jobName;
    private final Repository repository;

    public DelegateJobExecution(final JobExecution execution, final Repository repository) {
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
        } catch (final NoSuchJobExecutionException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public Date getStartTime() {
        try {
            return repository.getJobExecution(executionId).getStartTime();
        } catch (final NoSuchJobExecutionException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public Date getEndTime() {
        try {
            return repository.getJobExecution(executionId).getEndTime();
        } catch (final NoSuchJobExecutionException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public String getExitStatus() {
        try {
            return repository.getJobExecution(executionId).getExitStatus();
        } catch (final NoSuchJobExecutionException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public Date getCreateTime() {
        try {
            return repository.getJobExecution(executionId).getCreateTime();
        } catch (final NoSuchJobExecutionException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public Date getLastUpdatedTime() {
        try {
            return repository.getJobExecution(executionId).getLastUpdatedTime();
        } catch (final NoSuchJobExecutionException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public Properties getJobParameters() {
        try {
            return repository.getJobExecution(executionId).getJobParameters();
        } catch (final NoSuchJobExecutionException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public String getRestartElementId() {

        try {
            return repository.getJobExecution(executionId).getRestartElementId();
        } catch (final NoSuchJobExecutionException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public long getJobInstanceId() {
        try {
            return repository.getJobExecution(executionId).getJobInstanceId();
        } catch (final NoSuchJobExecutionException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }
}
