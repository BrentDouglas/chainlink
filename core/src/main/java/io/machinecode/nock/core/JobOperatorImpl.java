package io.machinecode.nock.core;

import io.machinecode.nock.core.configuration.ConfigurationFactoryImpl;
import io.machinecode.nock.core.configuration.RuntimeConfigurationImpl;
import io.machinecode.nock.core.factory.JobFactory;
import io.machinecode.nock.core.local.LocalTransportFactory;
import io.machinecode.nock.core.model.JobImpl;
import io.machinecode.nock.core.util.ResolvableService;
import io.machinecode.nock.core.impl.ContextImpl;
import io.machinecode.nock.core.work.Status;
import io.machinecode.nock.spi.ExecutionRepository;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.Job;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.transport.TransportFactory;
import io.machinecode.nock.spi.util.Message;
import io.machinecode.nock.spi.work.Deferred;
import io.machinecode.nock.spi.work.JobWork;

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
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobOperatorImpl implements JobOperator {

    private final RuntimeConfigurationImpl configuration;
    private final Transport transport;

    public JobOperatorImpl() {
        this.configuration = new RuntimeConfigurationImpl(ConfigurationFactoryImpl.INSTANCE.produce());

        final List<TransportFactory> transportFactories;
        try {
            transportFactories = new ResolvableService<TransportFactory>(TransportFactory.class).resolve(configuration.getClassLoader());
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        final TransportFactory transportFactory;
        if (transportFactories.isEmpty()) {
            transportFactory = new LocalTransportFactory();
        } else {
            transportFactory = transportFactories.get(0);
        }
        transport = transportFactory.produce(configuration, 1); //TODO
    }

    public JobOperatorImpl(final RuntimeConfigurationImpl configuration, final Transport transport) {
        this.configuration = configuration;
        this.transport = transport;
    }

    @Override
    public Set<String> getJobNames() throws JobSecurityException {
        return transport.getRepository().getJobNames();
    }

    @Override
    public int getJobInstanceCount(final String jobName) throws NoSuchJobException, JobSecurityException {
        return transport.getRepository().getJobInstanceCount(jobName);
    }

    @Override
    public List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws NoSuchJobException, JobSecurityException {
        return transport.getRepository().getJobInstances(jobName, start, count);
    }

    @Override
    public List<Long> getRunningExecutions(final String jobName) throws NoSuchJobException, JobSecurityException {
        return transport.getRepository().getRunningExecutions(jobName); //TODO This should probably go through Transport
    }

    @Override
    public Properties getParameters(final long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        return transport.getRepository().getParameters(executionId);
    }

    @Override
    public long start(final String jobXMLName, final Properties jobParameters) throws JobStartException, JobSecurityException {
        try {
            final Job theirs = configuration.getJobLoader().load(jobXMLName);
            final JobImpl job = JobFactory.INSTANCE.produceExecution(theirs, jobParameters);
            JobFactory.INSTANCE.validate(job);

            return start(job).id;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    public static class Start {
        public final long id;
        public final Deferred<?> deferred;

        public Start(final long id, final Deferred<?> deferred) {
            this.id = id;
            this.deferred = deferred;
        }
    }

    public Start start(final JobWork job) throws Exception {
        JobFactory.INSTANCE.validate(job);

        final ExecutionRepository repository = transport.getRepository();
        final JobInstance instance = repository.createJobInstance(job);
        final JobExecution execution = repository.createJobExecution(instance);
        final long jobExecutionId = execution.getExecutionId();
        final Context context = new ContextImpl(
                job,
                instance.getInstanceId(),
                jobExecutionId
        );
        Status.startedJob(repository, jobExecutionId);
        return new Start(
                jobExecutionId,
                transport.executeJob(jobExecutionId, job, context)
        );
    }

    @Override
    public long restart(final long executionId, final Properties restartParameters) throws JobExecutionAlreadyCompleteException, NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobRestartException, JobSecurityException {
        try {
            final ExecutionRepository repository = transport.getRepository();
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
            final JobInstance instance = repository.createJobInstance(job);
            final Context context = new ContextImpl(
                    job,
                    instance.getInstanceId(),
                    executionId
            );
            Status.startedJob(repository, executionId);
            transport.executeJob(executionId, job, context);
            return executionId;
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
        final ExecutionRepository repository = transport.getRepository();
        final Deferred<?> deferred = transport.getJob(executionId);
        if (deferred == null) {
            throw new JobExecutionNotRunningException();
        }
        Status.stoppingJob(repository, executionId);
        deferred.cancel(true);
        final Context context = transport.getContext(executionId);
        Status.stoppedJob(repository, executionId, context.getJobContext().getExitStatus());
    }

    @Override
    public void abandon(final long executionId) throws NoSuchJobExecutionException, JobExecutionIsRunningException, JobSecurityException {
        final ExecutionRepository repository = transport.getRepository();
        final JobExecution execution = repository.getJobExecution(executionId); // TODO Should we be getting this from the repo or transport
        if (Status.isRunning(execution.getBatchStatus())) {
            throw new JobExecutionIsRunningException();
        }
        Status.abandonedJob(repository, executionId);
    }

    @Override
    public JobInstance getJobInstance(final long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        return transport.getRepository().getJobInstance(executionId);
    }

    @Override
    public List<JobExecution> getJobExecutions(final JobInstance instance) throws NoSuchJobInstanceException, JobSecurityException {
        return transport.getRepository().getJobExecutions(instance);
    }

    @Override
    public JobExecution getJobExecution(final long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        return transport.getRepository().getJobExecution(executionId);
    }

    @Override
    public List<StepExecution> getStepExecutions(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        return transport.getRepository().getStepExecutions(jobExecutionId);
    }
}
