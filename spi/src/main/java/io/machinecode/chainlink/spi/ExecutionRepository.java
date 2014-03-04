package io.machinecode.chainlink.spi;

import io.machinecode.chainlink.spi.context.MutableMetric;
import io.machinecode.chainlink.spi.element.Job;
import io.machinecode.chainlink.spi.element.execution.Step;

import javax.batch.operations.JobExecutionNotMostRecentException;
import javax.batch.operations.JobRestartException;
import javax.batch.operations.JobSecurityException;
import javax.batch.operations.NoSuchJobException;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.operations.NoSuchJobInstanceException;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.Metric;
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

    ExtendedJobInstance createJobInstance(final Job job, final String jslName) throws Exception;

    ExtendedJobExecution createJobExecution(final ExtendedJobInstance jobInstance, final Properties parameters, final Date timestamp) throws Exception;

    /**
     * Must have batch status set to {@link BatchStatus#STARTING}.
     *
     * @param jobExecution
     * @param step
     * @return
     * @throws Exception
     */
    ExtendedStepExecution createStepExecution(final JobExecution jobExecution, final Step<?,?> step, final Date timestamp) throws Exception;

    PartitionExecution createPartitionExecution(final long stepExecutionId, final int partitionId, final Properties properties, final Date timestamp) throws Exception;

    PartitionExecution createPartitionExecution(final long stepExecutionId, final PartitionExecution partitionExecution, final Date timestamp) throws Exception;

    MutableMetric createMetric(final Metric.MetricType type);

    MutableMetric[] copyMetrics(final Metric[] metrics);

    // Update

    /**
     * The {@link JobExecution} with {@param jobExecutionId} MUST have its batch status set to {@link BatchStatus#STARTED}
     * after this method finishes. The start time must also be set.
     *
     * @param jobExecutionId
     * @param timestamp
     * @throws NoSuchJobExecutionException
     * @throws JobSecurityException
     */
    void startJobExecution(final long jobExecutionId, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException;

    void updateJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException;

    void finishJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final String exitStatus, final String restartElementId, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException;

    void linkJobExecutions(final long jobExecutionId, final ExtendedJobExecution restartJobExecution) throws NoSuchJobExecutionException, JobSecurityException;

    /**
     * The {@link StepExecution} with {@param stepExecutionId} MUST have its batch status set to {@link BatchStatus#STARTED}
     * after this method finishes.
     *
     * @param stepExecutionId
     * @param timestamp
     * @throws NoSuchJobExecutionException
     * @throws JobSecurityException
     */
    void startStepExecution(final long stepExecutionId, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException;

    void updateStepExecution(final long stepExecutionId, final Serializable persistentUserData, final Metric[] metrics, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException;

    void updateStepExecution(final long stepExecutionId, final Serializable persistentUserData, final Metric[] metrics, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException;

    void finishStepExecution(final long stepExecutionId, final BatchStatus batchStatus, final String exitStatus, final Metric[] metrics, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException;

    void updatePartitionExecution(final long stepExecutionId, final int partitionId, final Serializable persistentUserData, final Metric[] metrics, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException;

    void updatePartitionExecution(final long stepExecutionId, final int partitionId, final Serializable persistentUserData, final BatchStatus batchStatus, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException;

    void finishPartitionExecution(final long stepExecutionId, final int partitionId, final Serializable persistentUserData, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException;

    // JobOperator

    Set<String> getJobNames() throws JobSecurityException;

    int getJobInstanceCount(final String jobName) throws NoSuchJobException, JobSecurityException;

    List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws NoSuchJobException, JobSecurityException;

    List<Long> getRunningExecutions(final String jobName) throws NoSuchJobException, JobSecurityException;

    Properties getParameters(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException;

    ExtendedJobInstance getJobInstance(final long jobInstanceId) throws NoSuchJobExecutionException, JobSecurityException;

    ExtendedJobInstance getJobInstanceForExecution(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException;

    List<? extends JobExecution> getJobExecutions(final JobInstance instance) throws NoSuchJobInstanceException, JobSecurityException;

    ExtendedJobExecution getJobExecution(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException;

    ExtendedJobExecution restartJobExecution(final long jobExecutionId, final Properties parameters) throws JobRestartException, NoSuchJobExecutionException, NoSuchJobInstanceException, JobExecutionNotMostRecentException, JobSecurityException;

    List<? extends StepExecution> getStepExecutionsForJob(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException;

    ExtendedStepExecution getStepExecution(final long stepExecutionId) throws NoSuchJobExecutionException, JobSecurityException; // TODO Should throw something else

    /**
     *
     * @param jobExecutionId
     * @param stepExecutionId The id of the step execution currently running.
     * @param stepName
     * @return The latest step execution before the one currently running.
     * @throws NoSuchJobExecutionException
     * @throws JobSecurityException
     */
    ExtendedStepExecution getPreviousStepExecution(final long jobExecutionId, final long stepExecutionId, final String stepName) throws NoSuchJobExecutionException, JobSecurityException; // TODO Should throw something else

    ExtendedStepExecution getLatestStepExecution(final long jobExecutionId, final String stepName) throws NoSuchJobExecutionException, JobSecurityException; // TODO Should throw something else

    int getStepExecutionCount(final long jobExecutionId, final String stepName) throws NoSuchJobExecutionException, JobSecurityException; // TODO Should throw something else

    StepExecution[] getStepExecutions(final long[] stepExecutionIds) throws NoSuchJobExecutionException, JobSecurityException;

    PartitionExecution[] getUnfinishedPartitionExecutions(final long stepExecutionId) throws NoSuchJobExecutionException, JobSecurityException;

    PartitionExecution getPartitionExecution(final long stepExecutionId, final int partitionId) throws NoSuchJobExecutionException, JobSecurityException;
}
