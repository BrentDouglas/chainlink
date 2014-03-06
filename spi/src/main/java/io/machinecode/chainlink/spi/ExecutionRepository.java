package io.machinecode.chainlink.spi;

import io.machinecode.chainlink.spi.context.MutableMetric;
import io.machinecode.chainlink.spi.element.Job;
import io.machinecode.chainlink.spi.element.execution.Step;

import javax.batch.operations.JobSecurityException;
import javax.batch.operations.NoSuchJobExecutionException;
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

import static javax.batch.runtime.Metric.MetricType;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExecutionRepository {

    /**
     * @param job The id element of the job.
     * @param jslName The name of the xml file containing the job.
     * @return An instance of {@link ExtendedJobInstance} where:
     *         {@link ExtendedJobInstance#getJobName()} returns {@param job} and
     *         {@link ExtendedJobInstance#getJslName()} returns {@param jslName}.
     *
     *         This instance must be persisted in the repository and be available to subsequent calls to this repository.
     * @throws Exception On any error.
     */
    ExtendedJobInstance createJobInstance(final Job job, final String jslName) throws Exception;

    /**
     * @param jobInstance The {@link ExtendedJobInstance} to link this {@link ExtendedJobExecution} with.
     * @param parameters The parameters passed to this execution when it was started or restarted.
     * @param timestamp The current time on the JVM calling this method.
     * @return An instance of {@link ExtendedJobExecution} where:
     *         {@link ExtendedJobExecution#getBatchStatus()} returns {@link BatchStatus#STARTING},
     *         {@link ExtendedJobExecution#getExitStatus()} returns null,
     *         {@link ExtendedJobExecution#getCreateTime()} and {@link ExtendedJobExecution#getLastUpdatedTime()}
     *             return either {@param timestamp} or the current time of the repository server,
     *         {@link ExtendedJobExecution#getStartTime()} and {@link ExtendedJobExecution#getEndTime()} return null,
     *         {@link ExtendedJobExecution#getJobParameters()} returns either {@param parameters} and
     *         {@link ExtendedJobExecution#getRestartElementId()} returns null.
     *
     *         This instance must be persisted in the repository and be available to subsequent calls to this repository.
     * @throws Exception On any error.
     */
    ExtendedJobExecution createJobExecution(final ExtendedJobInstance jobInstance, final Properties parameters, final Date timestamp) throws Exception;

    /**
     * @param jobExecution The {@link ExtendedJobExecution} to link this {@link ExtendedStepExecution} with.
     * @param step The JSL step this execution represents.
     * @param timestamp The current time on the JVM calling this method.
     * @return An instance of {@link ExtendedStepExecution} where:
     *         {@link ExtendedStepExecution#getBatchStatus()} returns {@link BatchStatus#STARTING},
     *         {@link ExtendedStepExecution#getExitStatus()} returns null,
     *         {@link ExtendedStepExecution#getCreateTime()} and {@link ExtendedStepExecution#getUpdatedTime()}
     *             return either {@param timestamp} or the current time of the repository server,
     *         {@link ExtendedStepExecution#getStartTime()} and {@link ExtendedStepExecution#getEndTime()} return null,
     *         {@link ExtendedStepExecution#getPersistentUserData()} returns null,
     *         {@link ExtendedStepExecution#getReaderCheckpoint()} returns null,
     *         {@link ExtendedStepExecution#getWriterCheckpoint()} returns null and
     *         {@link ExtendedStepExecution#getMetrics()} returns an array of metrics with one for each {@link MetricType}
     *             where {@link Metric#getValue()} returns 0.
     *
     *         This instance must be persisted in the repository and be available to subsequent calls to this repository.
     * @throws Exception On any error.
     */
    ExtendedStepExecution createStepExecution(final JobExecution jobExecution, final Step<?,?> step, final Date timestamp) throws Exception;

    /**
     * @param stepExecutionId The id of the {@link ExtendedStepExecution} to link this {@link PartitionExecution} with.
     * @param partitionId The index of the partition within the step.
     * @param properties The parameters passed to this partition when it was started or restarted.
     * @param timestamp The current time on the JVM calling this method.
     * @return An instance of {@link PartitionExecution} where:
     *         {@link PartitionExecution#getStepExecutionId()} returns {@param stepExecutionId},
     *         {@link PartitionExecution#getPartitionId()} returns {@param partitionId},
     *         {@link PartitionExecution#getBatchStatus()} returns {@link BatchStatus#STARTING},
     *         {@link PartitionExecution#getExitStatus()} returns null,
     *         {@link PartitionExecution#getCreateTime()} and {@link PartitionExecution#getUpdatedTime()}
     *             return either {@param timestamp} or the current time of the repository server,
     *         {@link PartitionExecution#getStartTime()} and {@link PartitionExecution#getEndTime()} return null,
     *         {@link PartitionExecution#getPersistentUserData()} returns null,
     *         {@link PartitionExecution#getReaderCheckpoint()} returns null,
     *         {@link PartitionExecution#getWriterCheckpoint()} returns null,
     *         {@link PartitionExecution#getPartitionParameters()} returns {@param properties} and
     *         {@link PartitionExecution#getMetrics()} returns an array of metrics with one for each {@link MetricType}
     *             where {@link Metric#getValue()} returns 0.
     *
     *         This instance must be persisted in the repository and be available to subsequent calls to this repository.
     * @throws Exception On any error.
     */
    PartitionExecution createPartitionExecution(final long stepExecutionId, final int partitionId, final Properties properties, final Date timestamp) throws Exception;

    /**
     * @param stepExecutionId The id of the {@link ExtendedStepExecution} to link this {@link PartitionExecution} with.
     * @param partitionExecution A previous execution of this partition.
     * @param timestamp The current time on the JVM calling this method.
     * @return An instance of {@link PartitionExecution} where:
     *         {@link PartitionExecution#getStepExecutionId()} returns {@param stepExecutionId},
     *         {@link PartitionExecution#getPartitionId()} returns {@param partitionExecution#getPartitionId},
     *         {@link PartitionExecution#getBatchStatus()} returns {@link BatchStatus#STARTING},
     *         {@link PartitionExecution#getExitStatus()} returns null,
     *         {@link PartitionExecution#getCreateTime()} and {@link PartitionExecution#getUpdatedTime()}
     *             return either {@param timestamp} or the current time of the repository server,
     *         {@link PartitionExecution#getStartTime()} and {@link PartitionExecution#getEndTime()} return null,
     *         {@link PartitionExecution#getPersistentUserData()} returns {@param partitionExecution#getPersistentUserData},
     *         {@link PartitionExecution#getReaderCheckpoint()} returns {@param partitionExecution#getReaderCheckpoint},
     *         {@link PartitionExecution#getWriterCheckpoint()} returns {@param partitionExecution#getWriterCheckpoint},
     *         {@link PartitionExecution#getPartitionParameters()} returns {@param partitionExecution#getPartitionParameters} and
     *         {@link PartitionExecution#getMetrics()} returns an array of metrics with one for each {@link MetricType}
     *             where {@link Metric#getValue()} returns 0.
     *
     *         This instance must be persisted in the repository and be available to subsequent calls to this repository.
     * @throws Exception On any error.
     */
    PartitionExecution createPartitionExecution(final long stepExecutionId, final PartitionExecution partitionExecution, final Date timestamp) throws Exception;

    MutableMetric createMetric(final MetricType type);

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
    //NoSuchJobExecutionException, JobSecurityException,
    void startJobExecution(final long jobExecutionId, final Date timestamp) throws Exception;

    //NoSuchJobExecutionException, JobSecurityException,
    void updateJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final Date timestamp) throws Exception;

    //NoSuchJobExecutionException, JobSecurityException,
    void finishJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final String exitStatus, final String restartElementId, final Date timestamp) throws Exception;

    //NoSuchJobExecutionException, JobSecurityException,
    void linkJobExecutions(final long jobExecutionId, final ExtendedJobExecution restartJobExecution) throws Exception;

    /**
     * The {@link StepExecution} with {@param stepExecutionId} MUST have its batch status set to {@link BatchStatus#STARTED}
     * after this method finishes.
     *
     * @param stepExecutionId
     * @param timestamp
     * @throws NoSuchJobExecutionException
     * @throws JobSecurityException
     */
    //NoSuchJobExecutionException, JobSecurityException,
    void startStepExecution(final long stepExecutionId, final Date timestamp) throws Exception;

    //NoSuchJobExecutionException, JobSecurityException,
    void updateStepExecution(final long stepExecutionId, final Serializable persistentUserData, final Metric[] metrics, final Date timestamp) throws Exception;

    //NoSuchJobExecutionException, JobSecurityException,
    void updateStepExecution(final long stepExecutionId, final Serializable persistentUserData, final Metric[] metrics, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception;

    //NoSuchJobExecutionException, JobSecurityException,
    void finishStepExecution(final long stepExecutionId, final BatchStatus batchStatus, final String exitStatus, final Metric[] metrics, final Date timestamp) throws Exception;

    //NoSuchJobExecutionException, JobSecurityException,
    void updatePartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception;

    //NoSuchJobExecutionException, JobSecurityException,
    void updatePartitionExecution(final long partitionExecutionId, final Serializable persistentUserData, final BatchStatus batchStatus, final Date timestamp) throws Exception;

    //NoSuchJobExecutionException, JobSecurityException,
    void finishPartitionExecution(final long partitionExecutionId, final Serializable persistentUserData, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws Exception;

    // JobOperator

    //JobSecurityException,
    Set<String> getJobNames() throws Exception;

    //NoSuchJobException, JobSecurityException,
    int getJobInstanceCount(final String jobName) throws Exception;

    //NoSuchJobException, JobSecurityException,
    List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws Exception;

    //NoSuchJobException, JobSecurityException,
    List<Long> getRunningExecutions(final String jobName) throws Exception;

    //NoSuchJobExecutionException, JobSecurityException,
    Properties getParameters(final long jobExecutionId) throws Exception;

    //NoSuchJobExecutionException, JobSecurityException,
    ExtendedJobInstance getJobInstance(final long jobInstanceId) throws Exception;

    //NoSuchJobExecutionException, JobSecurityException,
    ExtendedJobInstance getJobInstanceForExecution(final long jobExecutionId) throws Exception;

    //NoSuchJobInstanceException, JobSecurityException,
    List<? extends JobExecution> getJobExecutions(final JobInstance instance) throws Exception;

    //NoSuchJobExecutionException, JobSecurityException,
    ExtendedJobExecution getJobExecution(final long jobExecutionId) throws Exception;

    //JobRestartException, NoSuchJobExecutionException, NoSuchJobInstanceException, JobExecutionNotMostRecentException, JobSecurityException,
    ExtendedJobExecution restartJobExecution(final long jobExecutionId, final Properties parameters) throws Exception;

    //NoSuchJobExecutionException, JobSecurityException,
    List<? extends StepExecution> getStepExecutionsForJob(final long jobExecutionId) throws Exception;

    //NoSuchJobExecutionException, JobSecurityException,
    ExtendedStepExecution getStepExecution(final long stepExecutionId) throws Exception;

    /**
     *
     * @param jobExecutionId
     * @param stepExecutionId The id of the step execution currently running.
     * @param stepName
     * @return The latest step execution before the one currently running.
     * @throws NoSuchJobExecutionException
     * @throws JobSecurityException
     */
    //NoSuchJobExecutionException, JobSecurityException,
    ExtendedStepExecution getPreviousStepExecution(final long jobExecutionId, final long stepExecutionId, final String stepName) throws Exception;

    //NoSuchJobExecutionException, JobSecurityException,
    ExtendedStepExecution getLatestStepExecution(final long jobExecutionId, final String stepName) throws Exception;

    //NoSuchJobExecutionException, JobSecurityException,
    int getStepExecutionCount(final long jobExecutionId, final String stepName) throws Exception;

    //NoSuchJobExecutionException, JobSecurityException,
    StepExecution[] getStepExecutions(final long[] stepExecutionIds) throws Exception;

    //NoSuchJobExecutionException, JobSecurityException,
    PartitionExecution[] getUnfinishedPartitionExecutions(final long stepExecutionId) throws Exception;

    //NoSuchJobExecutionException, JobSecurityException,
    PartitionExecution getPartitionExecution(final long partitionExecutionId) throws Exception;
}
