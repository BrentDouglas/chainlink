package io.machinecode.chainlink.spi.security;

import io.machinecode.chainlink.spi.Lifecycle;

import javax.batch.operations.JobSecurityException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Security extends Lifecycle {

    /**
     * @param jslName The jsl name being tested.
     * @throws JobSecurityException If a job with that name may not be started.
     * @see javax.batch.operations.JobOperator#start(String, java.util.Properties)
     * @see io.machinecode.chainlink.spi.management.ExtendedJobOperator#startJob(String, java.util.Properties)
     */
    void canStartJob(final String jslName) throws JobSecurityException;

    /**
     * @param jobExecutionId The id of the {@link javax.batch.runtime.JobExecution} being tested.
     * @throws JobSecurityException If the {@link javax.batch.runtime.JobExecution} may not be restarted.
     * @see javax.batch.operations.JobOperator#restart(long, java.util.Properties)
     * @see io.machinecode.chainlink.spi.management.ExtendedJobOperator#restartJob(long, java.util.Properties)
     */
    void canRestartJob(final long jobExecutionId) throws JobSecurityException;

    /**
     * @param jobExecutionId The id of the {@link javax.batch.runtime.JobExecution} being tested.
     * @throws JobSecurityException If the {@link javax.batch.runtime.JobExecution} may not be stopped.
     * @see javax.batch.operations.JobOperator#stop(long)
     * @see io.machinecode.chainlink.spi.management.ExtendedJobOperator#stopJob(long)
     */
    void canStopJob(final long jobExecutionId) throws JobSecurityException;

    /**
     * @param jobExecutionId The id of the {@link javax.batch.runtime.JobExecution} being tested.
     * @throws JobSecurityException If the {@link javax.batch.runtime.JobExecution} may not be abandoned.
     * @see javax.batch.operations.JobOperator#abandon(long)
     */
    void canAbandonJob(final long jobExecutionId) throws JobSecurityException;

    /**
     * @param jobName The job name being tested.
     * @throws JobSecurityException If the {@link javax.batch.runtime.JobInstance}'s are not accessible.
     * @see javax.batch.operations.JobOperator#getJobInstances(String, int, int)
     * @see javax.batch.operations.JobOperator#getJobInstanceCount(String)
     */
    void canAccessJob(final String jobName) throws JobSecurityException;

    /**
     * @param jobInstanceId The id of the {@link javax.batch.runtime.JobInstance} being tested.
     * @throws JobSecurityException If the {@link javax.batch.runtime.JobInstance} is not accessible.
     * @see javax.batch.operations.JobOperator#getJobExecutions(javax.batch.runtime.JobInstance)
     * @see javax.batch.operations.JobOperator#getJobInstance(long)
     * @see io.machinecode.chainlink.spi.management.ExtendedJobOperator#getJobInstanceById(long)
     */
    void canAccessJobInstance(final long jobInstanceId) throws JobSecurityException;

    /**
     * @param jobExecutionId The id of the {@link javax.batch.runtime.JobExecution} being tested.
     * @throws JobSecurityException If the {@link javax.batch.runtime.JobExecution} is not accessible.
     * @see javax.batch.operations.JobOperator#getJobExecution(long)
     * @see javax.batch.operations.JobOperator#getJobInstance(long)
     * @see javax.batch.operations.JobOperator#getParameters(long)
     * @see javax.batch.operations.JobOperator#getStepExecutions(long)
     * @see io.machinecode.chainlink.spi.management.ExtendedJobOperator#getJobOperation(long)
     */
    void canAccessJobExecution(final long jobExecutionId) throws JobSecurityException;

    /**
     * @param stepExecutionId The id of the {@link javax.batch.runtime.StepExecution} being tested.
     * @throws JobSecurityException If the {@link javax.batch.runtime.StepExecution} is not accessible.
     */
    void canAccessStepExecution(final long stepExecutionId) throws JobSecurityException;

    /**
     * @param partitionExecutionId The id of the {@link io.machinecode.chainlink.spi.repository.PartitionExecution}
     *                             being tested.
     * @throws JobSecurityException If the {@link io.machinecode.chainlink.spi.repository.PartitionExecution} is not
     *                              accessible.
     */
    void canAccessPartitionExecution(final long partitionExecutionId) throws JobSecurityException;

    /**
     * @param jobName The job name being tested.
     * @return {@code true} If the name of the job should be filtered.
     * @see javax.batch.operations.JobOperator#getJobNames()
     */
    boolean filterJobName(final String jobName);

    /**
     * @param jobInstanceId The id of the {@link javax.batch.runtime.JobInstance} being tested.
     * @return {@code true} If the {@link javax.batch.runtime.JobInstance} should be filtered.
     * @see javax.batch.operations.JobOperator#getJobInstances(String, int, int)
     */
    boolean filterJobInstance(final long jobInstanceId);

    /**
     * @param jobExecutionId The id of the {@link javax.batch.runtime.JobExecution} being tested.
     * @return {@code true} If the {@link javax.batch.runtime.JobExecution} should be filtered.
     * @see javax.batch.operations.JobOperator#getJobExecutions(javax.batch.runtime.JobInstance)
     * @see javax.batch.operations.JobOperator#getRunningExecutions(String)
     */
    boolean filterJobExecution(final long jobExecutionId);

    /**
     * @param stepExecutionId The id of the {@link javax.batch.runtime.StepExecution} being tested.
     * @return {@code true} If the {@link javax.batch.runtime.StepExecution} should be filtered.
     * @see javax.batch.operations.JobOperator#getStepExecutions(long)
     */
    boolean filterStepExecution(final long stepExecutionId);

    /**
     * @param partitionExecutionId The id of the {@link io.machinecode.chainlink.spi.repository.PartitionExecution}
     *                             being tested.
     * @return {@code true} If the {@link io.machinecode.chainlink.spi.repository.PartitionExecution} should be filtered.
     */
    boolean filterPartitionExecution(final long partitionExecutionId);
}
