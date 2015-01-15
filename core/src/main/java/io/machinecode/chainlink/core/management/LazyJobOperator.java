package io.machinecode.chainlink.core.management;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.element.Job;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.then.api.Promise;

import javax.batch.operations.BatchRuntimeException;
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

/**
 * Calls open on the first component invocation which some factories may require.
 *
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class LazyJobOperator implements ExtendedJobOperator {

    final JobOperatorImpl delegate;
    private transient Configuration configuration;

    public LazyJobOperator(final JobOperatorImpl delegate) {
        this.delegate = delegate;
    }

    synchronized void lazyOpen() {
        if (configuration != null) {
            try {
                delegate.open(configuration);
            } catch (final Exception e) {
                throw new BatchRuntimeException(e);
            }
            this.configuration = null;
        }
    }

    @Override
    public synchronized void open(final Configuration configuration) throws Exception {
        this.configuration = configuration;
    }

    @Override
    public void close() throws Exception {
        delegate.close();
    }

    @Override
    public Set<String> getJobNames() throws JobSecurityException {
        lazyOpen();
        return delegate.getJobNames();
    }

    @Override
    public int getJobInstanceCount(final String jobName) throws NoSuchJobException, JobSecurityException {
        lazyOpen();
        return delegate.getJobInstanceCount(jobName);
    }

    @Override
    public ExtendedJobInstance getJobInstanceById(final long jobInstanceId) throws NoSuchJobInstanceException, JobSecurityException {
        lazyOpen();
        return delegate.getJobInstanceById(jobInstanceId);
    }

    @Override
    public List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws NoSuchJobException, JobSecurityException {
        lazyOpen();
        return delegate.getJobInstances(jobName, start, count);
    }

    @Override
    public List<Long> getRunningExecutions(final String jobName) throws NoSuchJobException, JobSecurityException {
        lazyOpen();
        return delegate.getRunningExecutions(jobName);
    }

    @Override
    public Properties getParameters(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        lazyOpen();
        return delegate.getParameters(jobExecutionId);
    }

    @Override
    public long start(final String jslName, final Properties parameters) throws JobStartException, JobSecurityException {
        lazyOpen();
        return delegate.start(jslName, parameters);
    }

    @Override
    public JobOperationImpl startJob(final String jslName, final Properties parameters) throws JobStartException, JobSecurityException {
        lazyOpen();
        return delegate.startJob(jslName, parameters);
    }

    public JobOperationImpl startJob(final Job theirs, final String jslName, final Properties parameters) throws JobStartException, JobSecurityException {
        lazyOpen();
        return delegate.startJob(theirs, jslName, parameters);
    }

    @Override
    public JobOperationImpl getJobOperation(final long jobExecutionId) throws JobExecutionNotRunningException {
        lazyOpen();
        return delegate.getJobOperation(jobExecutionId);
    }

    @Override
    public long restart(final long jobExecutionId, final Properties parameters) throws JobExecutionAlreadyCompleteException, NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobRestartException, JobSecurityException {
        lazyOpen();
        return delegate.restart(jobExecutionId, parameters);
    }

    @Override
    public JobOperationImpl restartJob(final long jobExecutionId, final Properties parameters) throws JobExecutionAlreadyCompleteException, NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobRestartException, JobSecurityException {
        lazyOpen();
        return delegate.restartJob(jobExecutionId, parameters);
    }

    @Override
    public void stop(final long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException, JobSecurityException {
        lazyOpen();
        delegate.stop(jobExecutionId);
    }

    @Override
    public Promise<?, Throwable, ?> stopJob(final long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException, JobSecurityException {
        lazyOpen();
        return delegate.stopJob(jobExecutionId);
    }

    @Override
    public void abandon(final long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionIsRunningException, JobSecurityException {
        lazyOpen();
        delegate.abandon(jobExecutionId);
    }

    @Override
    public JobInstance getJobInstance(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        lazyOpen();
        return delegate.getJobInstance(jobExecutionId);
    }

    @Override
    public List<JobExecution> getJobExecutions(final JobInstance instance) throws NoSuchJobInstanceException, JobSecurityException {
        lazyOpen();
        return delegate.getJobExecutions(instance);
    }

    @Override
    public JobExecution getJobExecution(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        lazyOpen();
        return delegate.getJobExecution(jobExecutionId);
    }

    @Override
    public List<StepExecution> getStepExecutions(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        lazyOpen();
        return delegate.getStepExecutions(jobExecutionId);
    }
}
