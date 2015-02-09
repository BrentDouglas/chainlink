package io.machinecode.chainlink.spi.repository;

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
 * Implementations of this interface MUST be thread safe.
 *
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Repository {

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
     * @param jobId The id element of the job.
     * @param jslName The name of the xml file containing the job.
     * @param timestamp The current time on the JVM calling this method.
     * @return An instance of {@link ExtendedJobInstance} in accordance with the above rules.
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
     */
    ExtendedJobInstance createJobInstance(final String jobId, final String jslName, final Date timestamp) throws Exception;

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
     * @param jobInstanceId The id of the{@link ExtendedJobInstance} to link this {@link ExtendedJobExecution} with.
     * @param jobName TODO
     * @param parameters The parameters passed to this execution when it was started or restarted.
     * @param timestamp The current time on the JVM calling this method.
     * @return An instance of {@link ExtendedJobExecution} in accordance with the above rules.
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
     */
    ExtendedJobExecution createJobExecution(final long jobInstanceId, final String jobName, final Properties parameters, final Date timestamp) throws Exception;

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
     * {@link ExtendedStepExecution#getMetrics()} returns an array of metrics with one for each {@link Metric.MetricType}
     *     where {@link Metric#getValue()} returns 0.
     *
     * @param jobExecutionId The id of the {@link ExtendedJobExecution} to link this {@link ExtendedStepExecution} with.
     * @param stepName The id of the JSL step this execution represents.
     * @param timestamp The current time on the JVM calling this method.
     * @return An instance of {@link ExtendedStepExecution} in accordance with the above rules.
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
     */
    ExtendedStepExecution createStepExecution(final long jobExecutionId, final String stepName, final Date timestamp) throws Exception;

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
     * {@link PartitionExecution#getMetrics()} returns an array of metrics with one for each {@link Metric.MetricType}
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
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
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
     * in the javadoc for {@link #createJobExecution(long, String, java.util.Properties, java.util.Date)}.
     *
     * @param jobExecutionId The id of the {@link ExtendedJobExecution} that will be updated.
     * @param timestamp The current time on the JVM calling this method.
     * @throws javax.batch.operations.NoSuchJobExecutionException If this repository does not contain a resource
     * identified by {@param jobExecutionId}
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
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
     * @throws javax.batch.operations.NoSuchJobExecutionException If this repository does not contain a resource
     * identified by {@param jobExecutionId}
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
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
     * @throws javax.batch.operations.NoSuchJobExecutionException If this repository does not contain a resource
     * identified by {@param jobExecutionId}
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
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
     * @throws javax.batch.operations.NoSuchJobExecutionException If this repository does not contain a resource
     * identified by {@param jobExecutionId}
     *         or {@param restartJobExecutionId}.
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
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
     * in the javadoc for {@link #createStepExecution(long, String, java.util.Date)}.
     *
     * @param stepExecutionId The id of the {@link ExtendedStepExecution} that will be updated.
     * @param timestamp The current time on the JVM calling this method.
     * @throws javax.batch.operations.NoSuchJobExecutionException If this repository does not contain a resource
     * identified by {@param stepExecutionId}
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
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
     * @param persistentUserData From {@link javax.batch.runtime.context.StepContext#getPersistentUserData()}.
     * @param timestamp The current time on the JVM calling this method.
     * @throws javax.batch.operations.NoSuchJobExecutionException If this repository does not contain a resource
     * identified by {@param stepExecutionId}
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
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
     * @param persistentUserData From {@link javax.batch.runtime.context.StepContext#getPersistentUserData()}.
     * @param readerCheckpoint From {@link javax.batch.api.chunk.ItemReader#checkpointInfo()}.
     * @param writerCheckpoint From {@link javax.batch.api.chunk.ItemWriter#checkpointInfo()}.
     * @param timestamp The current time on the JVM calling this method.
     * @throws javax.batch.operations.NoSuchJobExecutionException If this repository does not contain a resource
     * identified by {@param stepExecutionId}
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
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
     * @throws javax.batch.operations.NoSuchJobExecutionException If this repository does not contain a resource identified by {@param stepExecutionId}
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
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
     * @throws javax.batch.operations.NoSuchJobExecutionException If this repository does not contain a resource
     * identified by {@param partitionExecutionId}
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
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
     * @param persistentUserData From {@link javax.batch.runtime.context.StepContext#getPersistentUserData()}.
     * @param readerCheckpoint From {@link javax.batch.api.chunk.ItemReader#checkpointInfo()}.
     * @param writerCheckpoint From {@link javax.batch.api.chunk.ItemWriter#checkpointInfo()}.
     * @param timestamp The current time on the JVM calling this method.
     * @throws javax.batch.operations.NoSuchJobExecutionException If this repository does not contain a resource identified by {@param partitionExecutionId}
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
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
     * @param persistentUserData From {@link javax.batch.runtime.context.StepContext#getPersistentUserData()}.
     * @param batchStatus From {@link javax.batch.runtime.context.StepContext#getBatchStatus()}
     * @param exitStatus From {@link javax.batch.runtime.context.StepContext#getExitStatus()}.
     * @param timestamp The current time on the JVM calling this method.
     * @throws javax.batch.operations.NoSuchJobExecutionException If this repository does not contain a resource
     * identified by {@param partitionExecutionId}
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
     */
    void finishPartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws Exception;

    /**
     * @see {@link javax.batch.operations.JobOperator#getJobNames()}
     *
     * @return A set of distinct job names that would be obtainable by calling either {@link JobInstance#getJobName()}
     * or {@link JobExecution#getJobName()} from any of the {@link JobInstance}'s or {@link JobExecution}'s stored in this
     * repository.
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
     */
    Set<String> getJobNames() throws Exception;

    /**
     * @see {@link javax.batch.operations.JobOperator#getJobInstanceCount(String)}
     *
     * @param jobName The search term.
     * @return The number of {@link JobInstance}'s that would return {@param jobName}
     * from {@link JobInstance#getJobName()}.
     * @throws javax.batch.operations.NoSuchJobException If this repository does not contain any {@link JobInstance}'s
     * that would return {@param jobName}
     * from {@link JobInstance#getJobName()}.
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
     */
    int getJobInstanceCount(final String jobName) throws Exception;

    /**
     * @see {@link javax.batch.operations.JobOperator#getJobInstances(String, int, int)}
     *
     * @param jobName The search term.
     * @param start The index of the first {@link JobInstance} to be included in the results.
     * @param count The maximum number of {@link JobInstance}'s to return.
     * @return A refined {@link List} of {@link JobInstance}'s matching the provided search parameters or an empty list,
     * MUST not return null. A matching {@link JobInstance} is defined as one where {@param jobName} would equal
     * the result of calling {@link JobInstance#getJobName()}.
     * The refined list is defined as an operation applied to a list of every match ordered by the result of
     * {@link javax.batch.runtime.JobInstance#getInstanceId()} where the first {@param start} of the
     * matching results are discarded and any more that {@param count} matching results are also discarded.
     * @throws javax.batch.operations.NoSuchJobException If this repository does not contain any matching {@link JobInstance}'s.
     * from {@link JobInstance#getJobName()}.
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
     */
    List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws Exception;

    /**
     * @see {@link javax.batch.operations.JobOperator#getRunningExecutions(String)}
     *
     * @param jobName The search term.
     * @return A refined {@link List} containing the results of calling {@link javax.batch.runtime.JobExecution#getExecutionId()}
     * on every matching {@link JobExecution} stored in this repository. MUST not be null.
     * A matching {@link JobExecution} is defined as one where {@param jobName} equals the result of calling
     * {@link javax.batch.runtime.JobExecution#getJobName()}.
     * The refined list is defined as an operation applied to a list of every match where the result of calling
     * {@link javax.batch.runtime.JobExecution#getBatchStatus()} is either {@link BatchStatus#STARTING} or
     * {@link BatchStatus#STARTED}. The list may be empty if the refining process excludes all matches.
     * @throws javax.batch.operations.NoSuchJobException If this repository does not contain any matching {@link JobExecution}'s.
     * from {@link JobExecution#getJobName()}.
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
     */
    List<Long> getRunningExecutions(final String jobName) throws Exception;

    /**
     * @see {@link javax.batch.operations.JobOperator#getParameters(long)}
     *
     * @param jobExecutionId The search term.
     * @return The result of calling {@link javax.batch.runtime.JobExecution#getJobParameters()} on the matching
     * {@link ExtendedJobExecution} stored in this repository. A matching {@link JobExecution} is defined as one where
     * calling {@link javax.batch.runtime.JobExecution#getExecutionId()} equals {@param jobExecutionId}.
     * @throws javax.batch.operations.NoSuchJobExecutionException If no matching {@link ExtendedJobExecution} is found.
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
     */
    Properties getParameters(final long jobExecutionId) throws Exception;

    /**
     * @see {@link io.machinecode.chainlink.spi.management.ExtendedJobOperator#getJobInstanceById(long)}
     *
     * @param jobInstanceId The search term.
     * @return The matching {@link ExtendedJobInstance} stored in this repository. A matching
     * {@link ExtendedJobInstance} is defined as one where the result of calling
     * {@link javax.batch.runtime.JobInstance#getInstanceId()} on it equals {@param jobInstanceId}.
     * @throws javax.batch.operations.NoSuchJobInstanceException If no matching {@link ExtendedJobInstance} is found.
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
     */
    ExtendedJobInstance getJobInstance(final long jobInstanceId) throws Exception;

    /**
     * @see {@link javax.batch.operations.JobOperator#getJobInstance(long)}
     *
     * @param jobExecutionId The search term.
     * @return The matching {@link ExtendedJobInstance} stored in this repository. A matching
     * {@link ExtendedJobInstance} is defined as one where the result of calling
     * {@link javax.batch.runtime.JobInstance#getInstanceId()} on it equals the result of calling
     * {@link io.machinecode.chainlink.spi.repository.ExtendedJobExecution#getJobInstanceId()} on a matching
     * {@link ExtendedJobExecution}. A matching {@link ExtendedJobExecution} is defined as one where the result of
     * calling {@link javax.batch.runtime.JobExecution#getExecutionId()} equals {@param jobExecutionId}.
     * @throws javax.batch.operations.NoSuchJobExecutionException If no matching {@link ExtendedJobInstance} is found.
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
     */
    ExtendedJobInstance getJobInstanceForExecution(final long jobExecutionId) throws Exception;

    /**
     * @see {@link javax.batch.operations.JobOperator#getJobExecutions(javax.batch.runtime.JobInstance)}
     *
     * @param jobInstanceId The search term.
     * @return A list of every matching {@link ExtendedJobExecution} stored in this repository. A matching
     * {@link JobExecution} is defined as one where calling {@link io.machinecode.chainlink.spi.repository.ExtendedJobExecution#getJobInstanceId()}
     * equals {@param jobInstanceId}.
     * @throws javax.batch.operations.NoSuchJobInstanceException If no matching {@link ExtendedJobExecution} is found.
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
     */
    List<? extends JobExecution> getJobExecutions(final long jobInstanceId) throws Exception;

    /**
     * @see {@link javax.batch.operations.JobOperator#getJobExecution(long)}
     *
     * @param jobExecutionId The search term.
     * @return The matching {@link ExtendedJobExecution} stored in this repository. A matching {@link JobExecution}
     * is defined as one where calling {@link javax.batch.runtime.JobExecution#getExecutionId()}
     * equals {@param jobExecutionId}.
     * @throws javax.batch.operations.NoSuchJobExecutionException If no matching {@link ExtendedJobExecution} is found.
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
     */
    ExtendedJobExecution getJobExecution(final long jobExecutionId) throws Exception;

    /**
     * @see {@link javax.batch.operations.JobOperator#restart(long, java.util.Properties)}
     *
     * @param jobExecutionId The search term.
     * @param parameters
     * @return TODO
     * @throws javax.batch.operations.NoSuchJobExecutionException If no matching {@link ExtendedJobExecution} is found.
     * @throws javax.batch.operations.JobExecutionNotMostRecentException If the matching {@link ExtendedJobExecution} is
     * not the most recent {@link ExtendedJobExecution} stored in the repository.
     * @throws javax.batch.operations.JobExecutionAlreadyCompleteException If the matching {@link ExtendedJobExecution}
     * returns {@link BatchStatus#COMPLETED} when {@link javax.batch.runtime.JobExecution#getBatchStatus()} is called.
     * @throws javax.batch.operations.JobRestartException If the matching {@link ExtendedJobExecution}
     * returns one of {@link BatchStatus#STARTED}, {@link BatchStatus#STARTING}, {@link BatchStatus#STOPPING} or
     * {@link BatchStatus#ABANDONED} when {@link javax.batch.runtime.JobExecution#getBatchStatus()} is called.
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
     */
    ExtendedJobExecution restartJobExecution(final long jobExecutionId, final Properties parameters) throws Exception;

    /**
     * @param jobExecutionId The {@link javax.batch.runtime.JobExecution#getExecutionId()} to search.
     * @return Every {@link StepExecution} that has been registered to this repository by calling {@link #createStepExecution(long, String, java.util.Date)}
     * where {@param jobExecutionId} matches the result of calling {@link javax.batch.runtime.JobExecution#getExecutionId()}
     * on the first parameter. This list may be empty if a {@link JobExecution} stored in this repository matches but no
     * {@link StepExecution}'s have been. MUST not return null.
     * @throws javax.batch.operations.NoSuchJobExecutionException If no {@link JobExecution} where calling
     * {@link javax.batch.runtime.JobExecution#getExecutionId()} would match {@param jobExecutionId} are stored in this
     * repository.
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
     */
    List<? extends StepExecution> getStepExecutionsForJobExecution(final long jobExecutionId) throws Exception;

    /**
     *
     * @param stepExecutionId
     * @return
     * @throws javax.batch.operations.NoSuchJobExecutionException
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
     */
    ExtendedStepExecution getStepExecution(final long stepExecutionId) throws Exception;

    /**
     * @param jobExecutionId The id on the {@link JobExecution} to search.
     * @param stepExecutionId The id of the step execution currently running.
     * @param stepName The name of the step to find.
     * @return The latest step execution before the one currently running.
     * @throws javax.batch.operations.NoSuchJobExecutionException
     * @throws javax.batch.operations.JobSecurityException
     * @throws Exception For implementation specific issues.
     */
    ExtendedStepExecution getPreviousStepExecution(final long jobExecutionId, final long stepExecutionId, final String stepName) throws Exception;

    /**
     * @param jobExecutionId The id on the {@link JobExecution} to search.
     * @param stepName The name of the step to find.
     * @return
     * @throws javax.batch.operations.NoSuchJobExecutionException
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
     */
    ExtendedStepExecution getLatestStepExecution(final long jobExecutionId, final String stepName) throws Exception;

    /**
     * @param jobExecutionId The id on the {@link JobExecution} to search.
     * @param stepName The name of the step to find.
     * @return The number of {@link StepExecution}'s with {@param stepName} in the sequence on job executions
     *         headed by {@param jobExecutionId} as described in the javadoc of {@link #linkJobExecutions(long, long)}.
     * @throws javax.batch.operations.NoSuchJobExecutionException
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
     */
    int getStepExecutionCount(final long jobExecutionId, final String stepName) throws Exception;

    /**
     *
     * @param stepExecutionIds
     * @return
     * @throws javax.batch.operations.NoSuchJobExecutionException
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
     */
    StepExecution[] getStepExecutions(final long[] stepExecutionIds) throws Exception;

    /**
     *
     * @param stepExecutionId
     * @return
     * @throws javax.batch.operations.NoSuchJobExecutionException
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
     */
    PartitionExecution[] getUnfinishedPartitionExecutions(final long stepExecutionId) throws Exception;

    /**
     *
     * @param partitionExecutionId
     * @return
     * @throws javax.batch.operations.NoSuchJobExecutionException
     * @throws javax.batch.operations.JobSecurityException For implementation specific security violations.
     * @throws Exception For implementation specific issues.
     */
    PartitionExecution getPartitionExecution(final long partitionExecutionId) throws Exception;
}
