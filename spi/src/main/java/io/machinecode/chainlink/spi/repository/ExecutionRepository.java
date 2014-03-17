package io.machinecode.chainlink.spi.repository;

import io.machinecode.chainlink.spi.element.Job;
import io.machinecode.chainlink.spi.element.execution.Step;

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

import static javax.batch.runtime.Metric.MetricType;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExecutionRepository {

    /**
     * This method must generate a {@code long} to be used as an identifier of the {@link ExtendedJobInstance} returned
     * which will hereafter be referred to as the jobInstanceId.
     *
     * Any {@link JobInstance} with the jobExecutionId obtained from this repository after this method completes
     * must behave as follows:
     *
     * {@link JobInstance#getJobName()} returns {@param job} and
     * {@link ExtendedJobInstance#getJslName()} returns {@param jslName}.
     *
     * @param job The id element of the job.
     * @param jslName The name of the xml file containing the job.
     * @param timestamp The current time on the JVM calling this method.
     * @return An instance of {@link ExtendedJobInstance} in accordance with the above rules.
     * @throws JobSecurityException TODO
     * @throws Exception When an error occurs.
     */
    ExtendedJobInstance createJobInstance(final Job job, final String jslName, final Date timestamp) throws Exception;

    /**
     * This method must generate a {@code long} to be used as an identifier of the {@link ExtendedJobExecution} returned
     * which will hereafter be referred to as the jobExecutionId.
     *
     * Any {@link JobExecution} with the jobExecutionId obtained from this repository after this method completes
     * must behave as follows until this behaviour is superseded by a call to {@link #startJobExecution(long, Date)},
     * {@link #updateJobExecution(long, BatchStatus, Date)} or {@link #finishJobExecution(long, BatchStatus, String, String, Date)}:
     *
     * {@link ExtendedJobExecution#getBatchStatus()} returns {@link BatchStatus#STARTING},
     * {@link ExtendedJobExecution#getExitStatus()} returns null,
     * {@link ExtendedJobExecution#getCreateTime()} and {@link ExtendedJobExecution#getLastUpdatedTime()}
     *     return return a {@link Date} equal to either {@param timestamp} or the time on the repository server
     *     when this method executes,
     * {@link ExtendedJobExecution#getStartTime()} and {@link ExtendedJobExecution#getEndTime()} return null,
     * {@link ExtendedJobExecution#getJobParameters()} returns either {@param parameters} and
     * {@link ExtendedJobExecution#getRestartElementId()} returns null.
     *
     * @param jobInstance The {@link ExtendedJobInstance} to link this {@link ExtendedJobExecution} with.
     * @param parameters The parameters passed to this execution when it was started or restarted.
     * @param timestamp The current time on the JVM calling this method.
     * @return An instance of {@link ExtendedJobExecution} in accordance with the above rules.
     * @throws JobSecurityException TODO
     * @throws Exception When an error occurs.
     */
    ExtendedJobExecution createJobExecution(final ExtendedJobInstance jobInstance, final Properties parameters, final Date timestamp) throws Exception;

    /**
     * This method must generate a {@code long} to be used as an identifier of the {@link ExtendedStepExecution} returned
     * which will hereafter be referred to as the stepExecutionId.
     *
     * Any {@link StepExecution} with the stepExecutionId obtained from this repository after this method completes
     * must behave as follows until this behaviour is superseded by a call to {@link #startStepExecution(long, Date)},
     * {@link #updateStepExecution(long, Metric[], Serializable, Date)} or
     * {@link #updateStepExecution(long, Metric[], Serializable, Serializable, Serializable, Date)} or
     * {@link #finishStepExecution(long, Metric[], BatchStatus, String, Date)}:
     *
     * {@link ExtendedStepExecution#getBatchStatus()} returns {@link BatchStatus#STARTING},
     * {@link ExtendedStepExecution#getExitStatus()} returns null,
     * {@link ExtendedStepExecution#getCreateTime()} and {@link ExtendedStepExecution#getUpdatedTime()}
     *     return return a {@link Date} equal to either {@param timestamp} or the time on the repository server
     *     when this method executes,
     * {@link ExtendedStepExecution#getStartTime()} and {@link ExtendedStepExecution#getEndTime()} return null,
     * {@link ExtendedStepExecution#getPersistentUserData()} returns null,
     * {@link ExtendedStepExecution#getReaderCheckpoint()} returns null,
     * {@link ExtendedStepExecution#getWriterCheckpoint()} returns null and
     * {@link ExtendedStepExecution#getMetrics()} returns an array of metrics with one for each {@link MetricType}
     *     where {@link Metric#getValue()} returns 0.
     *
     * @param jobExecution The {@link ExtendedJobExecution} to link this {@link ExtendedStepExecution} with.
     * @param step The JSL step this execution represents.
     * @param timestamp The current time on the JVM calling this method.
     * @return An instance of {@link ExtendedStepExecution} in accordance with the above rules.
     * @throws JobSecurityException TODO
     * @throws Exception When an error occurs.
     */
    ExtendedStepExecution createStepExecution(final JobExecution jobExecution, final Step<?,?> step, final Date timestamp) throws Exception;

    /**
     * This method must generate a {@code long} to be used as an identifier of the {@link PartitionExecution} returned
     * which will hereafter be referred to as the partitionExecutionId.
     *
     * Any {@link PartitionExecution} with the partitionExecutionId obtained from this repository after this method completes
     * must behave as follows until this behaviour is superseded by a call to {@link #startPartitionExecution(long, Date)}
     * {@link #updatePartitionExecution(long, Metric[], Serializable, Serializable, Serializable, Date)} or
     * {@link #finishPartitionExecution(long, Metric[], Serializable, BatchStatus, String, Date)}:
     *
     * {@link PartitionExecution#getStepExecutionId()} returns {@param stepExecutionId},
     * {@link PartitionExecution#getPartitionId()} returns {@param partitionId},
     * {@link PartitionExecution#getBatchStatus()} returns {@link BatchStatus#STARTING},
     * {@link PartitionExecution#getExitStatus()} returns null,
     * {@link PartitionExecution#getCreateTime()} and {@link PartitionExecution#getUpdatedTime()}
     *     return a {@link Date} equal to either {@param timestamp} or the time on the repository server
     *     when this method executes,
     * {@link PartitionExecution#getStartTime()} and {@link PartitionExecution#getEndTime()} return null,
     * {@link PartitionExecution#getPersistentUserData()} will return a copy of {@param persistentUserData} that has been
     *     serialized and deserialized and is not == to it,
     * {@link PartitionExecution#getReaderCheckpoint()} will return a copy of {@param readerCheckpoint} that has been
     *     serialized and deserialized and is not == to it,
     * {@link PartitionExecution#getWriterCheckpoint()} will return a copy of {@param writerCheckpoint} that has been
     *     serialized and deserialized and is not == to it
     * {@link PartitionExecution#getPartitionParameters()} returns {@param properties} and
     * {@link PartitionExecution#getMetrics()} returns an array of metrics with one for each {@link MetricType}
     *     where {@link Metric#getValue()} returns 0.
     *
     * @param stepExecutionId The id of the {@link ExtendedStepExecution} to link this {@link PartitionExecution} with.
     * @param partitionId The index of the partition within the step.
     * @param properties The parameters passed to this partition when it was started or restarted.
     * @param persistentUserData Some data from user space. May be null.
     * @param readerCheckpoint May be null.
     * @param writerCheckpoint May be null.
     * @param timestamp The current time on the JVM calling this method.
     * @return An instance of {@link PartitionExecution} in accordance with the above rules.
     * @throws JobSecurityException TODO
     * @throws Exception When an error occurs.
     */
    PartitionExecution createPartitionExecution(final long stepExecutionId, final int partitionId, final Properties properties, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception;

    /**
     * Any {@link JobExecution} with {@param jobExecutionId} obtained from this repository after this method completes
     * must behave as follows until this behaviour is superseded by a call to {@link #updateJobExecution(long, BatchStatus, Date)}
     * or {@link #finishJobExecution(long, BatchStatus, String, String, Date)}:
     *
     * {@link JobExecution#getBatchStatus()} will return {@link BatchStatus#STARTED},
     * {@link JobExecution#getStartTime()} and {@link JobExecution#getLastUpdatedTime()} will return a {@link Date} equal
     *     to either {@param timestamp} or the time on the repository server when this method executes.
     *
     * Other methods of {@link JobExecution} and {@link ExtendedJobExecution} not listed will return values as defined
     * in the javadoc for {@link #createJobExecution(ExtendedJobInstance, Properties, Date)}.
     *
     * @param jobExecutionId The id of the {@link ExtendedJobExecution} that will be updated.
     * @param timestamp The current time on the JVM calling this method.
     * @throws NoSuchJobExecutionException If this repository does not contain a resource identified by {@param jobExecutionId}
     * @throws JobSecurityException TODO
     * @throws Exception When an error occurs.
     */
    void startJobExecution(final long jobExecutionId, final Date timestamp) throws Exception;

    /**
     * Any {@link JobExecution} with {@param jobExecutionId} obtained from this repository after this method completes
     * must behave as follows until this behaviour is superseded by a call to
     * {@link #finishJobExecution(long, BatchStatus, String, String, Date)}:
     *
     * {@link JobExecution#getBatchStatus()} will return {@param batchStatus},
     * {@link JobExecution#getLastUpdatedTime()} will return a {@link Date} equal to either {@param timestamp} or the
     *     time on the repository server when this method executes.
     *
     * Other methods of {@link JobExecution} and {@link ExtendedJobExecution} not listed will return values as defined
     * in the javadoc for {@link #startJobExecution(long, Date)}.
     *
     * @param jobExecutionId The id of the {@link ExtendedJobExecution} that will be updated.
     * @param batchStatus The {@link BatchStatus} to update the {@link JobExecution} to.
     * @param timestamp The current time on the JVM calling this method.
     * @throws NoSuchJobExecutionException If this repository does not contain a resource identified by {@param jobExecutionId}
     * @throws JobSecurityException TODO
     * @throws Exception When an error occurs.
     */
    void updateJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final Date timestamp) throws Exception;

    /**
     * Any {@link JobExecution} with {@param jobExecutionId} obtained from this repository after this method completes
     * must behave as follows:
     *
     * {@link JobExecution#getBatchStatus()} will return {@param batchStatus},
     * {@link JobExecution#getExitStatus()} will return {@param exitStatus},
     * {@link ExtendedJobExecution#getRestartElementId()} will return {@param restartElementId},
     * {@link JobExecution#getEndTime()} and {@link JobExecution#getLastUpdatedTime()} will return a {@link Date} equal
     *     to either {@param timestamp} or the time on the repository server when this method executes.
     *
     * Other methods of {@link JobExecution} and {@link ExtendedJobExecution} not listed will return values as defined
     * in the javadoc for {@link #updateJobExecution(long, BatchStatus, Date)}.
     *
     * @param jobExecutionId The id of the {@link ExtendedJobExecution} that will be updated.
     * @param batchStatus The {@link BatchStatus} to update the {@link JobExecution} to.
     * @param exitStatus The exit status to update the {@link JobExecution} to.
     * @param restartElementId An optional
     * @param timestamp The current time on the JVM calling this method.
     * @throws NoSuchJobExecutionException If this repository does not contain a resource identified by {@param jobExecutionId}
     * @throws JobSecurityException TODO
     * @throws Exception When an error occurs.
     */
    void finishJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final String exitStatus, final String restartElementId, final Date timestamp) throws Exception;

    /**
     * Any {@link StepExecution} obtained from calls to {@link #getPreviousStepExecution(long, long, String)},
     * {@link #getStepExecutionCount(long, String)} and {@link #getLatestStepExecution(long, String)} (hereafter referred
     * to as the search method) with jobExecutionId's equal to {@param jobExecutionId} will obey the following semantics
     * once this method has executed:
     *
     * The set of {@link StepExecution}'s to search will not only include {@link StepExecution}'s identified by {@param jobExecutionId}
     * according to the procedure documented on the relevant search method, but will also add to that set to be searched
     * any {@link StepExecution}'s identified by {@param restartStepExecutionId}.
     *
     * Should this method have been called prior to the invocation of the search method with {@param restartJobExecutionId}
     * as the first parameter, the set of {@link StepExecution}'s will be expanded as described in the above paragraph.
     *
     * Essentially, each {@link JobExecution} may have one prior {@link JobExecution} and once linked the search set for
     * the search method's are expanded to encompass not only the previous {@link JobExecution} but any of it's previous
     * {@link JobExecution}'s.
     *
     * @param jobExecutionId The id of the later {@link ExtendedJobExecution}.
     * @param restartJobExecutionId The id of the earlier {@link ExtendedJobExecution}.
     * @throws NoSuchJobExecutionException If this repository does not contain a resource identified by {@param jobExecutionId}
     *         or {@param restartJobExecutionId}.
     * @throws JobSecurityException TODO
     * @throws Exception When an error occurs.
     */
    void linkJobExecutions(final long jobExecutionId, final long restartJobExecutionId) throws Exception;

    /**
     * Any {@link StepExecution} with {@param stepExecutionId} obtained from this repository after this method completes
     * must behave as follows until this behaviour is superseded by a call to
     * {@link #updateStepExecution(long, Metric[], Serializable, Date)} or
     * {@link #updateStepExecution(long, Metric[], Serializable, Serializable, Serializable, Date)} or
     * {@link #finishStepExecution(long, Metric[], BatchStatus, String, Date)}:
     *
     * {@link StepExecution#getBatchStatus()} will return {@link BatchStatus#STARTED},
     * {@link StepExecution#getStartTime()} and {@link ExtendedStepExecution#getUpdatedTime()} will return a {@link Date}
     *     equal to either {@param timestamp} or the time on the repository server when this method executes.
     *
     * Other methods of {@link StepExecution} and {@link ExtendedStepExecution} not listed will return values as defined
     * in the javadoc for {@link #createStepExecution(JobExecution, Step, Date)}.
     *
     * @param stepExecutionId The id of the {@link ExtendedStepExecution} that will be updated.
     * @param timestamp The current time on the JVM calling this method.
     * @throws NoSuchJobExecutionException If this repository does not contain a resource identified by {@param stepExecutionId}
     * @throws JobSecurityException TODO
     * @throws Exception When an error occurs.
     */
    void startStepExecution(final long stepExecutionId, final Date timestamp) throws Exception;

    /**
     * Any {@link StepExecution} with {@param stepExecutionId} obtained from this repository after this method completes
     * must behave as follows until this behaviour is superseded by a call to
     * {@link #finishStepExecution(long, Metric[], BatchStatus, String, Date)}:
     *
     * {@link StepExecution#getMetrics()} will return an array of {@link Metric}'s the same length as {@param metrics}
     *    such each element of the returned array has values returned from {@link Metric#getType()} and {@link Metric#getValue()}
     *    that are equal to the values of the same methods called on the {@link Metric} with the same index in {@param metrics},
     * {@link StepExecution#getPersistentUserData()} will return a copy of {@param persistentUserData} that has been
     *     serialized and deserialized and is not == to it,
     * {@link ExtendedStepExecution#getUpdatedTime()} will return a {@link Date} equal
     *     to either {@param timestamp} or the time on the repository server when this method executes.
     *
     * Other methods of {@link StepExecution} and {@link ExtendedStepExecution} not listed will return values as defined
     * in the javadoc for {@link #startStepExecution(long, Date)}.
     *
     *
     * @param stepExecutionId The id of the {@link io.machinecode.chainlink.spi.repository.ExtendedStepExecution} that will be updated.
     * @param metrics The final metrics of the step.
     * @param persistentUserData
     * @param timestamp The current time on the JVM calling this method.
     * @throws NoSuchJobExecutionException If this repository does not contain a resource identified by {@param stepExecutionId}
     * @throws JobSecurityException TODO
     * @throws Exception When an error occurs.
     */
    void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Date timestamp) throws Exception;

    /**
     * Any {@link StepExecution} with {@param stepExecutionId} obtained from this repository after this method completes
     * must behave as follows until this behaviour is superseded by a call to
     * {@link #finishStepExecution(long, Metric[], BatchStatus, String, Date)}:
     *
     * {@link StepExecution#getMetrics()} will return an array of {@link Metric}'s the same length as {@param metrics}
     *    such each element of the returned array has values returned from {@link Metric#getType()} and {@link Metric#getValue()}
     *    that are equal to the values of the same methods called on the {@link Metric} with the same index in {@param metrics},
     * {@link StepExecution#getPersistentUserData()} will return a copy of {@param persistentUserData} that has been
     *     serialized and deserialized and is not == to it,
     * {@link ExtendedStepExecution#getReaderCheckpoint()} will return a copy of {@param readerCheckpoint} that has been
     *     serialized and deserialized and is not == to it,
     * {@link ExtendedStepExecution#getWriterCheckpoint()} will return a copy of {@param writerCheckpoint} that has been
     *     serialized and deserialized and is not == to it
     * {@link ExtendedStepExecution#getUpdatedTime()} will return a {@link Date} equal
     *     to either {@param timestamp} or the time on the repository server when this method executes.
     *
     * Other methods of {@link StepExecution} and {@link ExtendedStepExecution} not listed will return values as defined
     * in the javadoc for {@link #startStepExecution(long, Date)}.
     *
     *
     * @param stepExecutionId The id of the {@link io.machinecode.chainlink.spi.repository.ExtendedStepExecution} that will be updated.
     * @param metrics The final metrics of the step.
     * @param persistentUserData
     * @param readerCheckpoint
     * @param writerCheckpoint
     * @param timestamp The current time on the JVM calling this method.
     * @throws NoSuchJobExecutionException If this repository does not contain a resource identified by {@param stepExecutionId}
     * @throws JobSecurityException TODO
     * @throws Exception When an error occurs.
     */
    void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception;

    /**
     * Any {@link StepExecution} with {@param stepExecutionId} obtained from this repository after this method completes
     * must behave as follows:
     *
     * {@link StepExecution#getMetrics()} will return an array of {@link Metric}'s the same length as {@param metrics}
     *    such each element of the returned array has values returned from {@link Metric#getType()} and {@link Metric#getValue()}
     *    that are equal to the values of the same methods called on the {@link Metric} with the same index in {@param metrics},
     * {@link StepExecution#getBatchStatus()} will return {@param batchStatus},
     * {@link StepExecution#getExitStatus()} will return {@param exitStatus},
     * {@link StepExecution#getEndTime()} and {@link ExtendedStepExecution#getUpdatedTime()} will return a {@link Date} equal
     *     to either {@param timestamp} or the time on the repository server when this method executes.
     *
     * Other methods of {@link StepExecution} and {@link ExtendedStepExecution} not listed will return values as defined
     * in the javadoc for {@link #updateStepExecution(long, Metric[], Serializable, Serializable, Serializable, Date)}.
     *
     *
     * @param stepExecutionId The id of the {@link io.machinecode.chainlink.spi.repository.ExtendedStepExecution} that will be updated.
     * @param metrics The final metrics of the step.
     * @param batchStatus The {@link BatchStatus} to update the {@link StepExecution} to.
     * @param exitStatus The exit status to update the {@link StepExecution} to.
     * @param timestamp The current time on the JVM calling this method.
     * @throws NoSuchJobExecutionException If this repository does not contain a resource identified by {@param stepExecutionId}
     * @throws JobSecurityException TODO
     * @throws Exception When an error occurs.
     */
    void finishStepExecution(final long stepExecutionId, final Metric[] metrics, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws Exception;

    /**
     * Any {@link PartitionExecution} with {@param partitionExecutionId} obtained from this repository after this method completes
     * must behave as follows until this behaviour is superseded by a call to
     * {@link #updatePartitionExecution(long, Metric[], Serializable, Serializable, Serializable, Date)} or
     * {@link #finishPartitionExecution(long, Metric[], Serializable, BatchStatus, String, Date)}.
     *
     * {@link PartitionExecution#getBatchStatus()} will return {@link BatchStatus#STARTED},
     * {@link PartitionExecution#getStartTime()} and {@link PartitionExecution#getUpdatedTime()} will return a {@link Date}
     *     equal to either {@param timestamp} or the time on the repository server when this method executes.
     *
     * Other methods of {@link PartitionExecution} not listed will return values as defined
     * in the javadoc for {@link #createPartitionExecution(long, int, Properties, Serializable, Serializable, Serializable, Date)}
     *
     * @param partitionExecutionId The id of the {@link PartitionExecution} that will be updated.
     * @param timestamp The current time on the JVM calling this method.
     * @throws NoSuchJobExecutionException If this repository does not contain a resource identified by {@param partitionExecutionId}
     * @throws JobSecurityException TODO
     * @throws Exception When an error occurs.
     */
    void startPartitionExecution(final long partitionExecutionId, final Date timestamp) throws Exception;

    /**
     * Any {@link PartitionExecution} with {@param partitionExecutionId} obtained from this repository after this method completes
     * must behave as follows until this behaviour is superseded by a call to
     * {@link #finishPartitionExecution(long, Metric[], Serializable, BatchStatus, String, Date)}.
     *
     * {@link PartitionExecution#getMetrics()} will return an array of {@link Metric}'s the same length as {@param metrics}
     *    such each element of the returned array has values returned from {@link Metric#getType()} and {@link Metric#getValue()}
     *    that are equal to the values of the same methods called on the {@link Metric} with the same index in {@param metrics},
     * {@link PartitionExecution#getPersistentUserData()} will return a copy of {@param persistentUserData} that has been
     *     serialized and deserialized and is not == to it,
     * {@link PartitionExecution#getReaderCheckpoint()} will return a copy of {@param readerCheckpoint} that has been
     *     serialized and deserialized and is not == to it,
     * {@link PartitionExecution#getWriterCheckpoint()} will return a copy of {@param writerCheckpoint} that has been
     *     serialized and deserialized and is not == to it
     * {@link PartitionExecution#getUpdatedTime()} will return a {@link Date} equal
     *     to either {@param timestamp} or the time on the repository server when this method executes.
     *
     * Other methods of {@link PartitionExecution} not listed will return values as defined
     * in the javadoc for {@link #startPartitionExecution(long, Date)}.
     *
     * @param partitionExecutionId The id of the {@link PartitionExecution} that will be updated.
     * @param metrics The current metrics of the partition.
     * @param persistentUserData
     * @param readerCheckpoint
     * @param writerCheckpoint
     * @param timestamp The current time on the JVM calling this method.
     * @throws NoSuchJobExecutionException If this repository does not contain a resource identified by {@param partitionExecutionId}
     * @throws JobSecurityException TODO
     * @throws Exception When an error occurs.
     */
    void updatePartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception;

    /**
     * Any {@link PartitionExecution} with {@param partitionExecutionId} obtained from this repository after this method completes
     * must behave as follows:
     *
     * {@link PartitionExecution#getMetrics()} will return an array of {@link Metric}'s the same length as {@param metrics}
     *    such each element of the returned array has values returned from {@link Metric#getType()} and {@link Metric#getValue()}
     *    that are equal to the values of the same methods called on the {@link Metric} with the same index in {@param metrics},
     * {@link PartitionExecution#getPersistentUserData()} will return a copy of {@param persistentUserData} that has been
     *     serialized and deserialized and is not == to it,
     * {@link PartitionExecution#getBatchStatus()} will return {@param batchStatus},
     * {@link PartitionExecution#getExitStatus()} will return {@param exitStatus},
     * {@link PartitionExecution#getEndTime()} and {@link PartitionExecution#getUpdatedTime()} will return a {@link Date} equal
     *     to either {@param timestamp} or the time on the repository server when this method executes.
     *
     * Other methods of {@link PartitionExecution} not listed will return values as defined
     * in the javadoc for {@link #updatePartitionExecution(long, Metric[], Serializable, Serializable, Serializable, Date)}.
     *
     * @param partitionExecutionId The id of the {@link PartitionExecution} that will be updated.
     * @param metrics The final metrics of the partition.
     * @param persistentUserData
     * @param batchStatus
     * @param exitStatus
     * @param timestamp The current time on the JVM calling this method.
     * @throws NoSuchJobExecutionException If this repository does not contain a resource identified by {@param partitionExecutionId}
     * @throws JobSecurityException TODO
     * @throws Exception When an error occurs.
     */
    void finishPartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws Exception;

    /**
     * @return A set of distinct job names that would be obtainable by calling either {@link JobInstance#getJobName()}
     * or {@link JobExecution#getJobName()} from any of the {@link JobInstance}'s or {@link JobExecution}'s stored in this
     * repository.
     * @throws JobSecurityException TODO
     * @throws Exception When an error occurs.
     */
    Set<String> getJobNames() throws Exception;

    /**
     * @param jobName The id of the {@link Job} to search for instances of.
     * @return The number of {@link JobInstance}'s that would return {@param jobName}
     * from {@link JobInstance#getJobName()}.
     * @throws NoSuchJobException If this repository does not contain any {@link JobInstance}'s that would return {@param jobName}
     * from {@link JobInstance#getJobName()}.
     * @throws JobSecurityException TODO
     * @throws Exception When an error occurs.
     */
    int getJobInstanceCount(final String jobName) throws Exception;

    /**
     *
     * @param jobName The id of the {@link Job} to search for instances of.
     * @param start
     * @param count
     * @return
     * @throws NoSuchJobException If this repository does not contain any {@link JobInstance}'s that would return {@param jobName}
     * from {@link JobInstance#getJobName()}.
     * @throws JobSecurityException TODO
     * @throws Exception When an error occurs.
     */
    List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws Exception;

    /**
     *
     * @param jobName
     * @return
     * @throws NoSuchJobException If this repository does not contain any {@link JobExecution}'s that would return {@param jobName}
     * from {@link JobExecution#getJobName()}.
     * @throws JobSecurityException TODO
     * @throws Exception When an error occurs.
     */
    List<Long> getRunningExecutions(final String jobName) throws Exception;

    /**
     *
     * @param jobExecutionId
     * @return
     * @throws NoSuchJobExecutionException
     * @throws JobSecurityException TODO
     * @throws Exception
     */
    Properties getParameters(final long jobExecutionId) throws Exception;

    /**
     *
     * @param jobInstanceId
     * @return
     * @throws NoSuchJobExecutionException
     * @throws JobSecurityException TODO
     * @throws Exception
     */
    ExtendedJobInstance getJobInstance(final long jobInstanceId) throws Exception;

    /**
     *
     * @param jobExecutionId
     * @return
     * @throws NoSuchJobExecutionException
     * @throws JobSecurityException TODO
     * @throws Exception
     */
    ExtendedJobInstance getJobInstanceForExecution(final long jobExecutionId) throws Exception;

    /**
     *
     * @param instance
     * @return
     * @throws NoSuchJobInstanceException
     * @throws JobSecurityException TODO
     * @throws Exception
     */
    List<? extends JobExecution> getJobExecutions(final JobInstance instance) throws Exception;

    /**
     *
     * @param jobExecutionId
     * @return
     * @throws NoSuchJobExecutionException
     * @throws JobSecurityException TODO
     * @throws Exception
     */
    ExtendedJobExecution getJobExecution(final long jobExecutionId) throws Exception;

    //JobRestartException, NoSuchJobExecutionException, NoSuchJobInstanceException, JobExecutionNotMostRecentException, JobSecurityException,

    /**
     *
     * @param jobExecutionId
     * @param parameters
     * @return
     * @throws NoSuchJobExecutionException
     * @throws JobSecurityException TODO
     * @throws Exception
     */
    ExtendedJobExecution restartJobExecution(final long jobExecutionId, final Properties parameters) throws Exception;

    //NoSuchJobExecutionException, JobSecurityException,

    /**
     *
     * @param jobExecutionId
     * @return
     * @throws NoSuchJobExecutionException
     * @throws JobSecurityException TODO
     * @throws Exception
     */
    List<? extends StepExecution> getStepExecutionsForJobExecution(final long jobExecutionId) throws Exception;

    /**
     *
     * @param stepExecutionId
     * @return
     * @throws NoSuchJobExecutionException
     * @throws JobSecurityException TODO
     * @throws Exception
     */
    ExtendedStepExecution getStepExecution(final long stepExecutionId) throws Exception;

    /**
     *
     * @param jobExecutionId
     * @param stepExecutionId The id of the step execution currently running.
     * @param stepName
     * @return The latest step execution before the one currently running.
     * @throws NoSuchJobExecutionException
     * @throws JobSecurityException
     * @throws Exception
     */
    ExtendedStepExecution getPreviousStepExecution(final long jobExecutionId, final long stepExecutionId, final String stepName) throws Exception;

    /**
     *
     * @param jobExecutionId
     * @param stepName
     * @return
     * @throws NoSuchJobExecutionException
     * @throws JobSecurityException TODO
     * @throws Exception
     */
    ExtendedStepExecution getLatestStepExecution(final long jobExecutionId, final String stepName) throws Exception;

    /**
     *
     * @param jobExecutionId
     * @param stepName
     * @return
     * @throws NoSuchJobExecutionException
     * @throws JobSecurityException TODO
     * @throws Exception
     */
    int getStepExecutionCount(final long jobExecutionId, final String stepName) throws Exception;

    /**
     *
     * @param stepExecutionIds
     * @return
     * @throws NoSuchJobExecutionException
     * @throws JobSecurityException TODO
     * @throws Exception
     */
    StepExecution[] getStepExecutions(final long[] stepExecutionIds) throws Exception;

    /**
     *
     * @param stepExecutionId
     * @return
     * @throws NoSuchJobExecutionException
     * @throws JobSecurityException TODO
     * @throws Exception
     */
    PartitionExecution[] getUnfinishedPartitionExecutions(final long stepExecutionId) throws Exception;

    /**
     *
     * @param partitionExecutionId
     * @return
     * @throws NoSuchJobExecutionException
     * @throws JobSecurityException TODO
     * @throws Exception
     */
    PartitionExecution getPartitionExecution(final long partitionExecutionId) throws Exception;
}
