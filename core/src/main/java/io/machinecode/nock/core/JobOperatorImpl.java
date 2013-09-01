package io.machinecode.nock.core;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.nock.core.configuration.ConfigurationFactoryImpl;
import io.machinecode.nock.core.configuration.RuntimeConfigurationImpl;
import io.machinecode.nock.core.factory.JobFactory;
import io.machinecode.nock.core.model.JobImpl;
import io.machinecode.nock.core.work.ContextImpl;
import io.machinecode.nock.core.work.Status;
import io.machinecode.nock.core.util.PropertiesConverter;
import io.machinecode.nock.core.util.ResolvableService;
import io.machinecode.nock.core.work.WorkerImpl;
import io.machinecode.nock.spi.Repository;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.Job;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.transport.TransportFactory;
import io.machinecode.nock.spi.util.Message;

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
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.StepExecution;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobOperatorImpl implements JobOperator {

    private final RuntimeConfigurationImpl configuration;
    private final TransportFactory transportFactory;

    private final TMap<Long, Future<?>> jobs = new THashMap<Long, Future<?>>();
    final AtomicBoolean lock = new AtomicBoolean(false);

    private Future<?> get(final long executionId) {
        while (!lock.compareAndSet(false, true)) {}
        try {
            return this.jobs.get(executionId);
        } finally {
            lock.set(false);
        }
    }

    private void put(final long executionId, final  Future<?> future) {
        while (!lock.compareAndSet(false, true)) {}
        try {
            this.jobs.put(executionId, future);
        } finally {
            lock.set(false);
        }
    }


    public JobOperatorImpl() {
        this.configuration = new RuntimeConfigurationImpl(ConfigurationFactoryImpl.INSTANCE.produce());

        final List<TransportFactory> transportFactories;
        try {
            transportFactories = new ResolvableService<TransportFactory>(TransportFactory.class).resolve(configuration.getClassLoader());
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (transportFactories.isEmpty()) {
            throw new RuntimeException();
        } else {
            this.transportFactory = transportFactories.get(0);
        }
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
        try {
            final Job theirs = configuration.getJobLoader().load(jobXMLName);
            final JobImpl job = JobFactory.INSTANCE.produceExecution(theirs, jobParameters);
            JobFactory.INSTANCE.validate(job);

            final Repository repository = configuration.getRepository();
            final Transport transport = transportFactory.produce(repository);
            final JobInstance instance = repository.createJobInstance(job);
            final JobExecution execution = repository.createJobExecution(instance);
            final Context context = new ContextImpl(
                    job,
                    instance.getInstanceId(),
                    execution.getExecutionId(),
                    new long[0]
            );
            Status.started(transport, context);
            final Future<?> stop = transport.runJob(job, context);
            put(execution.getExecutionId(), stop);
            return execution.getExecutionId();
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    @Override
    public long restart(final long executionId, final Properties restartParameters) throws JobExecutionAlreadyCompleteException, NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobRestartException, JobSecurityException {
        try {
            final Repository repository = configuration.getRepository();
            final JobExecution execution = repository.getLatestJobExecution(executionId);
            if (BatchStatus.STOPPED.equals(execution.getBatchStatus())
                    || BatchStatus.STOPPED.equals(execution.getBatchStatus())) {
                throw new JobRestartException(Message.cantRestartBatchStatus(execution.getExecutionId(), execution.getBatchStatus()));
            }
            final Job theirs = configuration.getJobLoader().load(execution.getJobName());
            final JobImpl job = JobFactory.INSTANCE.produceExecution(theirs, restartParameters);
            JobFactory.INSTANCE.validate(job);
            if (!job.isRestartable()) {
                throw new JobRestartException(Message.cantRestartJob(execution.getExecutionId()));
            }
            if (BatchStatus.COMPLETED.equals(execution.getBatchStatus())) {
                throw new JobExecutionAlreadyCompleteException();
            }
            final Transport transport = transportFactory.produce(repository);
            final JobInstance instance = repository.createJobInstance(job);
            final Context context = new ContextImpl(
                    job,
                    instance.getInstanceId(),
                    execution.getExecutionId(),
                    new long[0]
            );
            Status.started(transport, context);
            final Future<?> stop = transport.runJob(job, context);
            put(execution.getExecutionId(), stop);
            return execution.getExecutionId();
        } catch (final JobRestartException e) {
            throw e;
        } catch (final JobExecutionAlreadyCompleteException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobRestartException(e);
        }
    }

    @Override
    public void stop(final long executionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException, JobSecurityException {
        final Repository repository = configuration.getRepository();
        final Future<?> future = get(executionId);
        if (future == null) {
            throw new JobExecutionNotRunningException();
        }
        future.cancel(true);
        repository.updateJobExecution(executionId, BatchStatus.STOPPING, new Date());
    }

    @Override
    public void abandon(final long executionId) throws NoSuchJobExecutionException, JobExecutionIsRunningException, JobSecurityException {
        final Repository repository = configuration.getRepository();
        final JobExecution execution = repository.getJobExecution(executionId);
        if (Status.isRunning(execution.getBatchStatus())) {
            throw new JobExecutionIsRunningException();
        }
        repository.updateJobExecution(executionId, BatchStatus.ABANDONED, new Date());
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
