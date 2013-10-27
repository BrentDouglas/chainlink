package io.machinecode.nock.spi;

import io.machinecode.nock.spi.element.Job;
import io.machinecode.nock.spi.element.execution.Step;

import javax.batch.operations.JobExecutionNotMostRecentException;
import javax.batch.operations.JobSecurityException;
import javax.batch.operations.NoSuchJobException;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.operations.NoSuchJobInstanceException;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.StepExecution;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExecutionRepository {

    // Create

    JobInstance createJobInstance(final Job job) throws Exception;

    JobExecution createJobExecution(final JobInstance jobInstance) throws Exception;

    /**
     * Must have batch status set to {@link BatchStatus#STARTING} and exit status set to {@code STARTING}
     *
     * @param jobExecution
     * @param step
     * @return
     * @throws Exception
     */
    StepExecution createStepExecution(final JobExecution jobExecution, final Step<?,?> step) throws Exception;

    // Update

    /**
     * The {@link JobExecution} with {@param executionId} MUST have its batch status set to {@link BatchStatus#STARTED}
     * after this method finishes.
     *
     * @param jobExecutionId
     * @param timestamp
     * @throws NoSuchJobExecutionException
     * @throws JobSecurityException
     */
    void startJobExecution(final long jobExecutionId, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException;

    void updateJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException;

    void finishJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException;

    void updateStepExecution(final long stepExecutionId, final BatchStatus batchStatus, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException;

    void updateStepExecution(final long stepExecutionId, final Serializable serializable, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException;

    void updateStepExecution(final long stepExecutionId, final Serializable serializable, final Checkpoint checkpoint, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException;

    void finishStepExecution(final long stepExecutionId, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException;

    // Chunk

    Checkpoint getStepExecutionCheckpoint(final long stepExecutionId) throws NoSuchJobExecutionException, JobSecurityException;

    // JobOperator

    Set<String> getJobNames() throws JobSecurityException;

    int getJobInstanceCount(final String jobName) throws NoSuchJobException, JobSecurityException;

    List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws NoSuchJobException, JobSecurityException;

    List<Long> getRunningExecutions(final String jobName) throws NoSuchJobException, JobSecurityException;

    Properties getParameters(final long executionId) throws NoSuchJobExecutionException, JobSecurityException;

    JobInstance getJobInstance(final long instanceId) throws NoSuchJobExecutionException, JobSecurityException;

    JobInstance getJobInstanceForExecution(final long executionId) throws NoSuchJobExecutionException, JobSecurityException;

    List<JobExecution> getJobExecutions(final JobInstance instance) throws NoSuchJobInstanceException, JobSecurityException;

    JobExecution getJobExecution(final long executionId) throws NoSuchJobExecutionException, JobSecurityException;

    JobExecution getLatestJobExecution(final long executionId) throws NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobSecurityException;

    List<StepExecution> getStepExecutions(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException;

    //

    StepExecution getStepExecution(final long stepExecutionId) throws NoSuchJobExecutionException, JobSecurityException; // ? Should throw someting else ?

    StepExecution[] getStepExecutions(final long[] stepExecutionIds) throws NoSuchJobExecutionException, JobSecurityException;
}
