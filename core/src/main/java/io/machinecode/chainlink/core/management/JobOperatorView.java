package io.machinecode.chainlink.core.management;

import io.machinecode.chainlink.core.Chainlink;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import io.machinecode.chainlink.spi.management.JobOperation;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import org.jboss.logging.Logger;

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
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JobOperatorView implements ExtendedJobOperator {

    private static final Logger log = Logger.getLogger(JobOperatorView.class);

    private final ExtendedJobOperator delegate;

    public JobOperatorView() {
        this(Constants.DEFAULT);
    }

    public JobOperatorView(final String id) {
        this(lookupOperator(id));
    }

    public JobOperatorView(final ExtendedJobOperator delegate) {
        this.delegate = delegate;
    }

    private static ExtendedJobOperator lookupOperator(final String id) {
        try {
            return Chainlink.getEnvironment().getJobOperator(id);
        } catch (final RuntimeException e) {
            log.error("Failed to locate the required ExtendedJobOperator", e); //TODO Message
            throw e;
        } catch (final Exception e) {
            log.error("Failed to locate the required ExtendedJobOperator", e); //TODO Message
            throw new RuntimeException(e);
        }
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
    public ExtendedJobInstance getJobInstance(final long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        return delegate.getJobInstance(executionId);
    }

    @Override
    public List<JobExecution> getJobExecutions(final JobInstance instance) throws NoSuchJobInstanceException, JobSecurityException {
        return delegate.getJobExecutions(instance);
    }

    @Override
    public ExtendedJobExecution getJobExecution(final long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        return delegate.getJobExecution(executionId);
    }

    @Override
    public List<StepExecution> getStepExecutions(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        return delegate.getStepExecutions(jobExecutionId);
    }

    @Override
    public void open(final Configuration configuration) throws Exception {
        delegate.open(configuration);
    }

    @Override
    public void close() throws Exception {
        delegate.close();
    }
}
