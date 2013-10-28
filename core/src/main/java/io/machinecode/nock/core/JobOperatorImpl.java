package io.machinecode.nock.core;

import io.machinecode.nock.core.configuration.ConfigurationFactoryImpl;
import io.machinecode.nock.core.configuration.RuntimeConfigurationImpl;
import io.machinecode.nock.core.factory.JobFactory;
import io.machinecode.nock.core.impl.ContextImpl;
import io.machinecode.nock.core.impl.DelegateJobExecutionImpl;
import io.machinecode.nock.core.impl.DelegateStepExecutionImpl;
import io.machinecode.nock.core.local.LocalTransportFactory;
import io.machinecode.nock.core.model.JobImpl;
import io.machinecode.nock.core.util.ResolvableService;
import io.machinecode.nock.core.work.Status;
import io.machinecode.nock.spi.ExecutionRepository;
import io.machinecode.nock.spi.RestartableJobExecution;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.Job;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.transport.TransportFactory;
import io.machinecode.nock.spi.util.Message;
import io.machinecode.nock.spi.work.Deferred;
import io.machinecode.nock.spi.work.JobWork;
import org.jboss.logging.Logger;

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
import javax.batch.runtime.context.JobContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobOperatorImpl implements JobOperator {

    private static final Logger log = Logger.getLogger(JobOperatorImpl.class);

    private final RuntimeConfigurationImpl configuration;
    private final Transport transport;

    //TODO This whole business needs sorting out
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
        log.tracef(Message.get("operator.start"), jobXMLName);
        try {
            final Job theirs = configuration.getJobLoader().load(jobXMLName);
            final JobImpl job = JobFactory.INSTANCE.produceExecution(theirs, jobParameters);

            return start(job).id;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    public Start start(final JobWork job) throws Exception {
        JobFactory.INSTANCE.validate(job);

        final ExecutionRepository repository = transport.getRepository();
        final JobInstance instance = repository.createJobInstance(job);
        final RestartableJobExecution execution = repository.createJobExecution(instance);
        final long jobExecutionId = execution.getExecutionId();
        final Context context = new ContextImpl(
                job,
                instance.getInstanceId(),
                execution
        );
        Status.startingJob(repository, jobExecutionId);
        return new Start(
                jobExecutionId,
                transport.executeJob(jobExecutionId, job, context)
        );
    }

    @Override
    public long restart(final long executionId, final Properties restartParameters) throws JobExecutionAlreadyCompleteException, NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobRestartException, JobSecurityException {
        log.tracef(Message.get("operator.restart"), executionId);
        try {
            final ExecutionRepository repository = transport.getRepository();
            final RestartableJobExecution execution = repository.getLatestJobExecution(executionId);
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
                    execution
            );
            Status.startingJob(repository, executionId);
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
        log.tracef(Message.get("operator.stop"), executionId);
        final ExecutionRepository repository = transport.getRepository();
        //TODO Race? Although if the job handle is evicted before the context we will be right. This needs to be synchronised by the transport
        final Context context = transport.getContext(executionId);
        final JobContext jobContext = context == null ? null : context.getJobContext();
        final Deferred<?> deferred = transport.getJob(executionId);
        if (deferred == null) {
            throw new JobExecutionNotRunningException();
        }
        Status.stoppingJob(repository, executionId);
        deferred.cancel(true);
        Status.stoppedJob(repository, executionId, jobContext == null ? null : jobContext.getExitStatus());
    }

    @Override
    public void abandon(final long executionId) throws NoSuchJobExecutionException, JobExecutionIsRunningException, JobSecurityException {
        log.tracef(Message.get("operator.abandon"), executionId);
        final ExecutionRepository repository = transport.getRepository();
        final JobExecution execution = repository.getJobExecution(executionId); // TODO Should we be getting this from the repo or transport
        if (Status.isRunning(execution.getBatchStatus())) {
            throw new JobExecutionIsRunningException();
        }
        Status.abandonedJob(repository, executionId);
    }

    @Override
    public JobInstance getJobInstance(final long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        return transport.getRepository().getJobInstanceForExecution(executionId);
    }

    @Override
    public List<JobExecution> getJobExecutions(final JobInstance instance) throws NoSuchJobInstanceException, JobSecurityException {
        //return transport.getRepository().getJobExecutions(instance);
        final ExecutionRepository repository = transport.getRepository();
        final List<JobExecution> x =  repository.getJobExecutions(instance);
        final List<JobExecution> delegates = new ArrayList<JobExecution>(x.size());
        for (final JobExecution execution : x) {
            delegates.add(new DelegateJobExecutionImpl(execution, repository));
        }
        return delegates;
    }

    @Override
    public JobExecution getJobExecution(final long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        //return transport.getRepository().getJobExecution(executionId);
        return new DelegateJobExecutionImpl(transport.getRepository().getJobExecution(executionId), transport.getRepository());
    }

    @Override
    public List<StepExecution> getStepExecutions(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        //return transport.getRepository().getStepExecutions(jobExecutionId);
        final ExecutionRepository repository = transport.getRepository();
        final List<StepExecution> x =  repository.getStepExecutions(jobExecutionId);
        final List<StepExecution> delegates = new ArrayList<StepExecution>(x.size());
        for (final StepExecution execution : x) {
            delegates.add(new DelegateStepExecutionImpl(execution, repository));
        }
        return delegates;
    }

    public static class Start {
        public final long id;
        public final Deferred<?> deferred;

        public Start(final long id, final Deferred<?> deferred) {
            this.id = id;
            this.deferred = deferred;
        }
    }
}
