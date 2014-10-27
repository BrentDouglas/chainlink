package io.machinecode.chainlink.spi.management;

import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;

import javax.batch.operations.JobExecutionAlreadyCompleteException;
import javax.batch.operations.JobExecutionNotMostRecentException;
import javax.batch.operations.JobExecutionNotRunningException;
import javax.batch.operations.JobOperator;
import javax.batch.operations.JobRestartException;
import javax.batch.operations.JobSecurityException;
import javax.batch.operations.JobStartException;
import javax.batch.operations.NoSuchJobExecutionException;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExtendedJobOperator extends JobOperator, AutoCloseable {

    ExtendedJobInstance getJobInstanceById(final long jobInstanceId);

    JobOperation startJob(final String jslName, final Properties parameters) throws JobStartException, JobSecurityException;

    JobOperation getJobOperation(final long jobExecutionId) throws JobExecutionNotRunningException;

    JobOperation restartJob(final long jobExecutionId, final Properties parameters) throws JobExecutionAlreadyCompleteException, NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobRestartException, JobSecurityException;

    Future<?> stopJob(final long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException, JobSecurityException;
}
