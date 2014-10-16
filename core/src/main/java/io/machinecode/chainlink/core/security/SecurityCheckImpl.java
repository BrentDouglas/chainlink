package io.machinecode.chainlink.core.security;

import io.machinecode.chainlink.spi.security.SecurityCheck;

import javax.batch.operations.JobSecurityException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class SecurityCheckImpl implements SecurityCheck {

    private final SecurityCheck[] securityChecks;

    public SecurityCheckImpl(final SecurityCheck... securityChecks) {
        this.securityChecks = securityChecks;
    }

    @Override
    public void canStartJob(final String jslName) throws JobSecurityException {
        for (final SecurityCheck securityCheck : securityChecks) {
            securityCheck.canStartJob(jslName);
        }
    }

    @Override
    public void canRestartJob(final long jobExecutionId) throws JobSecurityException {
        for (final SecurityCheck securityCheck : securityChecks) {
            securityCheck.canRestartJob(jobExecutionId);
        }
    }

    @Override
    public void canStopJob(final long jobExecutionId) throws JobSecurityException {
        for (final SecurityCheck securityCheck : securityChecks) {
            securityCheck.canStopJob(jobExecutionId);
        }
    }

    @Override
    public void canAbandonJob(final long jobExecutionId) throws JobSecurityException {
        for (final SecurityCheck securityCheck : securityChecks) {
            securityCheck.canAbandonJob(jobExecutionId);
        }
    }

    @Override
    public void canAccessJob(final String jobName) throws JobSecurityException {
        for (final SecurityCheck securityCheck : securityChecks) {
            securityCheck.canAccessJob(jobName);
        }
    }

    @Override
    public void canAccessJobInstance(final long jobInstanceId) throws JobSecurityException {
        for (final SecurityCheck securityCheck : securityChecks) {
            securityCheck.canAccessJobInstance(jobInstanceId);
        }
    }

    @Override
    public void canAccessJobExecution(final long jobExecutionId) throws JobSecurityException {
        for (final SecurityCheck securityCheck : securityChecks) {
            securityCheck.canAccessJobExecution(jobExecutionId);
        }
    }

    @Override
    public void canAccessStepExecution(final long stepExecutionId) throws JobSecurityException {
        for (final SecurityCheck securityCheck : securityChecks) {
            securityCheck.canAccessStepExecution(stepExecutionId);
        }
    }

    @Override
    public void canAccessPartitionExecution(final long partitionExecutionId) throws JobSecurityException {
        for (final SecurityCheck securityCheck : securityChecks) {
            securityCheck.canAccessPartitionExecution(partitionExecutionId);
        }
    }

    @Override
    public boolean filterJobName(final String jobName) {
        for (final SecurityCheck securityCheck : securityChecks) {
            if (securityCheck.filterJobName(jobName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean filterJobInstance(final long jobInstanceId) {
        for (final SecurityCheck securityCheck : securityChecks) {
            if (securityCheck.filterJobInstance(jobInstanceId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean filterJobExecution(final long jobExecutionId) {
        for (final SecurityCheck securityCheck : securityChecks) {
            if (securityCheck.filterJobExecution(jobExecutionId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean filterStepExecution(final long stepExecutionId) {
        for (final SecurityCheck securityCheck : securityChecks) {
            if (securityCheck.filterStepExecution(stepExecutionId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean filterPartitionExecution(final long partitionExecutionId) {
        for (final SecurityCheck securityCheck : securityChecks) {
            if (securityCheck.filterPartitionExecution(partitionExecutionId)) {
                return true;
            }
        }
        return false;
    }
}
