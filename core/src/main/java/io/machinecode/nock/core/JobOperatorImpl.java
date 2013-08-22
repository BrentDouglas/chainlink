package io.machinecode.nock.core;

import io.machinecode.nock.core.factory.JobFactory;
import io.machinecode.nock.core.inject.ResolvableService;
import io.machinecode.nock.core.model.JobImpl;
import io.machinecode.nock.spi.configuration.Configuration;
import io.machinecode.nock.spi.configuration.ConfigurationFactory;
import io.machinecode.nock.spi.element.Job;

import javax.batch.operations.JobExecutionAlreadyCompleteException;
import javax.batch.operations.JobExecutionIsRunningException;
import javax.batch.operations.JobExecutionNotMostRecentException;
import javax.batch.operations.JobExecutionNotRunningException;
import javax.batch.operations.JobOperator;
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
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobOperatorImpl implements JobOperator {

    private final Configuration configuration;

    public JobOperatorImpl() {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        final List<ConfigurationFactory> factories;
        try {
            factories = new ResolvableService<ConfigurationFactory>(ConfigurationFactory.class).resolve(tccl);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (factories.isEmpty()) {
            throw new RuntimeException();
        }
        this.configuration = factories.get(0).produce();
    }

    @Override
    public Set<String> getJobNames() throws JobSecurityException {
        return configuration.getRepository().getJobNames();
    }

    @Override
    public int getJobInstanceCount(final String jobName) throws NoSuchJobException, JobSecurityException {
        return configuration.getRepository().getJobInstanceCount(jobName);
    }

    @Override
    public List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws NoSuchJobException, JobSecurityException {
        return configuration.getRepository().getJobInstances(jobName, start, count);
    }

    @Override
    public List<Long> getRunningExecutions(final String jobName) throws NoSuchJobException, JobSecurityException {
        return configuration.getRepository().getRunningExecutions(jobName);
    }

    @Override
    public Properties getParameters(final long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        return configuration.getRepository().getParameters(executionId);
    }

    @Override
    public long start(final String jobXMLName, final Properties jobParameters) throws JobStartException, JobSecurityException {
        final Job theirs = configuration.getRepository().getJob(jobXMLName);
        final JobImpl job = JobFactory.INSTANCE.produceExecution(theirs, jobParameters);
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long restart(final long executionId, final Properties restartParameters) throws JobExecutionAlreadyCompleteException, NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobRestartException, JobSecurityException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void stop(final long executionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException, JobSecurityException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void abandon(final long executionId) throws NoSuchJobExecutionException, JobExecutionIsRunningException, JobSecurityException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public JobInstance getJobInstance(final long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        return configuration.getRepository().getJobInstance(executionId);
    }

    @Override
    public List<JobExecution> getJobExecutions(final JobInstance instance) throws NoSuchJobInstanceException, JobSecurityException {
        return configuration.getRepository().getJobExecutions(instance);
    }

    @Override
    public JobExecution getJobExecution(final long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        return configuration.getRepository().getJobExecution(executionId);
    }

    @Override
    public List<StepExecution> getStepExecutions(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        return configuration.getRepository().getStepExecutions(jobExecutionId);
    }
}
