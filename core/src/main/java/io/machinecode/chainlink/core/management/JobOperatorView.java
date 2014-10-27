package io.machinecode.chainlink.core.management;

import io.machinecode.chainlink.core.Chainlink;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.element.Job;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import io.machinecode.chainlink.spi.management.JobOperation;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;

import javax.batch.operations.JobExecutionAlreadyCompleteException;
import javax.batch.operations.JobExecutionIsRunningException;
import javax.batch.operations.JobExecutionNotMostRecentException;
import javax.batch.operations.JobExecutionNotRunningException;
import javax.batch.operations.JobRestartException;
import javax.batch.operations.JobSecurityException;
import javax.batch.operations.JobStartException;
import javax.batch.operations.NoSuchJobException;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.operations.NoSuchJobInstanceException;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.StepExecution;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobOperatorView implements ExtendedJobOperator {

    private final ExtendedJobOperator delegate;

    public JobOperatorView() {
        this(Constants.DEFAULT_CONFIGURATION);
    }

    public JobOperatorView(final String id) {
        this(Chainlink.getEnvironment().getJobOperator(id));
    }

    public JobOperatorView(final ExtendedJobOperator delegate) {
        this.delegate = delegate;
    }

    @Override
    public ExtendedJobInstance getJobInstanceById(final long jobInstanceId) {
        return delegate.getJobInstanceById(jobInstanceId);
    }

    @Override
    public JobOperation startJob(final String jslName, final Properties parameters) throws JobStartException, JobSecurityException {
        return delegate.startJob(jslName, parameters);
    }

    @Override
    public JobOperation getJobOperation(final long jobExecutionId) throws JobExecutionNotRunningException {
        return delegate.getJobOperation(jobExecutionId);
    }

    @Override
    public JobOperation restartJob(final long jobExecutionId, final Properties parameters) throws JobExecutionAlreadyCompleteException, NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobRestartException, JobSecurityException {
        return delegate.restartJob(jobExecutionId, parameters);
    }

    @Override
    public Future<?> stopJob(final long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException, JobSecurityException {
        return delegate.stopJob(jobExecutionId);
    }

    @Override
    public Set<String> getJobNames() throws JobSecurityException {
        return delegate.getJobNames();
    }

    @Override
    public int getJobInstanceCount(final String jobName) throws NoSuchJobException, JobSecurityException {
        return delegate.getJobInstanceCount(jobName);
    }

    @Override
    public List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws NoSuchJobException, JobSecurityException {
        return delegate.getJobInstances(jobName, start, count);
    }

    @Override
    public List<Long> getRunningExecutions(final String jobName) throws NoSuchJobException, JobSecurityException {
        return delegate.getRunningExecutions(jobName);
    }

    @Override
    public Properties getParameters(final long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        return delegate.getParameters(executionId);
    }

    @Override
    public long start(final String jobXMLName, final Properties jobParameters) throws JobStartException, JobSecurityException {
        return delegate.start(jobXMLName, jobParameters);
    }

    @Override
    public long restart(final long executionId, final Properties restartParameters) throws JobExecutionAlreadyCompleteException, NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobRestartException, JobSecurityException {
        return delegate.restart(executionId, restartParameters);
    }

    @Override
    public void stop(final long executionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException, JobSecurityException {
        delegate.stop(executionId);
    }

    @Override
    public void abandon(final long executionId) throws NoSuchJobExecutionException, JobExecutionIsRunningException, JobSecurityException {
        delegate.abandon(executionId);
    }

    @Override
    public JobInstance getJobInstance(final long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        return delegate.getJobInstance(executionId);
    }

    @Override
    public List<JobExecution> getJobExecutions(final JobInstance instance) throws NoSuchJobInstanceException, JobSecurityException {
        return delegate.getJobExecutions(instance);
    }

    @Override
    public JobExecution getJobExecution(final long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        return delegate.getJobExecution(executionId);
    }

    @Override
    public List<StepExecution> getStepExecutions(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        return delegate.getStepExecutions(jobExecutionId);
    }

    @Override
    public void close() throws Exception {
        delegate.close();
    }
}
