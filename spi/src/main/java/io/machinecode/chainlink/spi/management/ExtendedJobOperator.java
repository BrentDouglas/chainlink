package io.machinecode.chainlink.spi.management;

import io.machinecode.chainlink.spi.Lifecycle;
import io.machinecode.chainlink.spi.work.JobWork;

import javax.batch.operations.JobExecutionAlreadyCompleteException;
import javax.batch.operations.JobExecutionNotMostRecentException;
import javax.batch.operations.JobExecutionNotRunningException;
import javax.batch.operations.JobOperator;
import javax.batch.operations.JobRestartException;
import javax.batch.operations.JobSecurityException;
import javax.batch.operations.JobStartException;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.management.MXBean;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@MXBean
public interface ExtendedJobOperator extends JobOperator, Lifecycle {

    JobOperation startJob(final JobWork job, final String jslName, final Properties parameters) throws JobStartException, JobSecurityException;

    JobOperation getJobOperation(final long jobExecutionId) throws JobExecutionNotRunningException;

    JobOperation restartJob(final long jobExecutionId, final Properties parameters) throws JobExecutionAlreadyCompleteException, NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobRestartException, JobSecurityException;

    Future<?> stopJob(final long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException, JobSecurityException;
}
