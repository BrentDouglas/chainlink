package io.machinecode.chainlink.spi.security;

import javax.batch.operations.JobSecurityException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Security {

    void canStartJob(final String jslName) throws JobSecurityException;

    void canRestartJob(final long jobExecutionId) throws JobSecurityException;

    void canStopJob(final long jobExecutionId) throws JobSecurityException;

    void canAbandonJob(final long jobExecutionId) throws JobSecurityException;

    void canAccessJob(final String jobName) throws JobSecurityException;

    void canAccessJobInstance(final long jobInstanceId) throws JobSecurityException;

    void canAccessJobExecution(final long jobExecutionId) throws JobSecurityException;

    void canAccessStepExecution(final long stepExecutionId) throws JobSecurityException;

    void canAccessPartitionExecution(final long partitionExecutionId) throws JobSecurityException;

    boolean filterJobName(final String jobName);

    boolean filterJobInstance(final long jobInstanceId);

    boolean filterJobExecution(final long jobExecutionId);

    boolean filterStepExecution(final long stepExecutionId);

    boolean filterPartitionExecution(final long partitionExecutionId);
}
