package io.machinecode.nock.core;

import io.machinecode.nock.core.configuration.ConfigurationFactoryImpl;
import io.machinecode.nock.core.configuration.RuntimeConfigurationImpl;
import io.machinecode.nock.core.exec.EventedExecutorFactory;
import io.machinecode.nock.core.factory.JobFactory;
import io.machinecode.nock.core.impl.ExecutionContextImpl;
import io.machinecode.nock.core.impl.DelegateJobExecutionImpl;
import io.machinecode.nock.core.impl.DelegateStepExecutionImpl;
import io.machinecode.nock.core.model.JobImpl;
import io.machinecode.nock.core.util.ResolvableService;
import io.machinecode.nock.core.work.JobExecutable;
import io.machinecode.nock.core.work.RepositoryStatus;
import io.machinecode.nock.spi.ExecutionRepository;
import io.machinecode.nock.spi.RestartableJobExecution;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.element.Job;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.execution.ExecutorFactory;
import io.machinecode.nock.spi.util.Messages;
import io.machinecode.nock.spi.deferred.Deferred;
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
    private final Executor executor;

    //TODO This whole business needs sorting out
    public JobOperatorImpl() {
        this.configuration = new RuntimeConfigurationImpl(ConfigurationFactoryImpl.INSTANCE.produce());

        final List<ExecutorFactory> transportFactories;
        try {
            transportFactories = new ResolvableService<ExecutorFactory>(ExecutorFactory.class).resolve(configuration.getClassLoader());
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        final ExecutorFactory executorFactory;
        if (transportFactories.isEmpty()) {
            executorFactory = new EventedExecutorFactory();
        } else {
            executorFactory = transportFactories.get(0);
        }
        executor = executorFactory.produce(configuration, 1); //TODO
    }

    public JobOperatorImpl(final RuntimeConfigurationImpl configuration, final Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

    @Override
    public Set<String> getJobNames() throws JobSecurityException {
        return executor.getRepository().getJobNames();
    }

    @Override
    public int getJobInstanceCount(final String jobName) throws NoSuchJobException, JobSecurityException {
        return executor.getRepository().getJobInstanceCount(jobName);
    }

    @Override
    public List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws NoSuchJobException, JobSecurityException {
        return executor.getRepository().getJobInstances(jobName, start, count);
    }

    @Override
    public List<Long> getRunningExecutions(final String jobName) throws NoSuchJobException, JobSecurityException {
        return executor.getRepository().getRunningExecutions(jobName); //TODO This should probably go through Transport
    }

    @Override
    public Properties getParameters(final long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        return executor.getRepository().getParameters(executionId);
    }

    @Override
    public long start(final String jobXMLName, final Properties jobParameters) throws JobStartException, JobSecurityException {
        log.tracef(Messages.get("operator.start"), jobXMLName);
        try {
            final Job theirs = configuration.getJobLoader().load(jobXMLName);
            final JobImpl job = JobFactory.INSTANCE.produceExecution(theirs, jobParameters);

            return start(job).getJobExecutionId();
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    public JobOperationImpl start(final JobWork job) throws Exception {
        JobFactory.INSTANCE.validate(job);

        final ExecutionRepository repository = executor.getRepository();
        final JobInstance instance = repository.createJobInstance(job);
        final RestartableJobExecution execution = repository.createJobExecution(instance);
        final long jobExecutionId = execution.getExecutionId();
        final ExecutionContext context = new ExecutionContextImpl(
                job,
                job.getId(),
                instance.getInstanceId(),
                execution
        );
        RepositoryStatus.startingJob(repository, jobExecutionId);
        final Deferred<?,?> deferred = executor.execute(new JobExecutable(job, context));
        //TODO put jobs jobExecutionId, deferred
        return new JobOperationImpl(
                jobExecutionId,
                deferred,
                repository
        );
    }

    @Override
    public long restart(final long executionId, final Properties restartParameters) throws JobExecutionAlreadyCompleteException, NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobRestartException, JobSecurityException {
        log.tracef(Messages.get("operator.restart"), executionId);
        try {
            final ExecutionRepository repository = executor.getRepository();
            final RestartableJobExecution execution = repository.getLatestJobExecution(executionId);
            if (!(BatchStatus.STOPPED.equals(execution.getBatchStatus())
                    || BatchStatus.FAILED.equals(execution.getBatchStatus()))) {
                throw new JobRestartException(Messages.cantRestartBatchStatus(execution.getExecutionId(), execution.getBatchStatus()));
            }
            final Job theirs = configuration.getJobLoader().load(execution.getJobName());
            final JobImpl job = JobFactory.INSTANCE.produceExecution(theirs, restartParameters);
            JobFactory.INSTANCE.validate(job);
            if (!job.isRestartable()) {
                throw new JobRestartException(Messages.cantRestartJob(execution.getExecutionId()));
            }
            if (BatchStatus.COMPLETED.equals(execution.getBatchStatus())) {
                throw new JobExecutionAlreadyCompleteException();
            }
            final JobInstance instance = repository.createJobInstance(job);
            final ExecutionContext context = new ExecutionContextImpl(
                    job,
                    job.getId(),
                    instance.getInstanceId(),
                    execution
            );
            RepositoryStatus.startingJob(repository, executionId);
            final Deferred<?,?> deferred = executor.execute(new JobExecutable(job, context));
            //TODO put jobs jobExecutionId, deferred
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
        log.tracef(Messages.get("operator.stop"), executionId);
        final ExecutionRepository repository = executor.getRepository();
        repository.getJobExecution(executionId); //This will throw a NoSuchJobExecutionException if required
        final Deferred<?,?> deferred = executor.getJob(executionId);
        if (deferred == null) {
            throw new JobExecutionNotRunningException();
        }
        RepositoryStatus.stoppingJob(repository, executionId);
        deferred.cancel(true);
        RepositoryStatus.stoppedJob(repository, executionId, null);
    }

    @Override
    public void abandon(final long executionId) throws NoSuchJobExecutionException, JobExecutionIsRunningException, JobSecurityException {
        log.tracef(Messages.get("operator.abandon"), executionId);
        final ExecutionRepository repository = executor.getRepository();
        final JobExecution execution = repository.getJobExecution(executionId); // TODO Should we be getting this from the repo or execution
        if (RepositoryStatus.isRunning(execution.getBatchStatus())) {
            throw new JobExecutionIsRunningException();
        }
        RepositoryStatus.abandonedJob(repository, executionId);
    }

    @Override
    public JobInstance getJobInstance(final long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        return executor.getRepository().getJobInstanceForExecution(executionId);
    }

    @Override
    public List<JobExecution> getJobExecutions(final JobInstance instance) throws NoSuchJobInstanceException, JobSecurityException {
        //return execution.getRepository().getJobExecutions(instance);
        final ExecutionRepository repository = executor.getRepository();
        final List<JobExecution> executions =  repository.getJobExecutions(instance);
        final List<JobExecution> delegates = new ArrayList<JobExecution>(executions.size());
        for (final JobExecution execution : executions) {
            delegates.add(new DelegateJobExecutionImpl(execution, repository));
        }
        return delegates;
    }

    @Override
    public JobExecution getJobExecution(final long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        //return execution.getRepository().getJobExecution(executionId);
        return new DelegateJobExecutionImpl(executor.getRepository().getJobExecution(executionId), executor.getRepository());
    }

    @Override
    public List<StepExecution> getStepExecutions(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        //return execution.getRepository().getStepExecutions(jobExecutionId);
        final ExecutionRepository repository = executor.getRepository();
        final List<StepExecution> executions =  repository.getStepExecutions(jobExecutionId);
        final List<StepExecution> delegates = new ArrayList<StepExecution>(executions.size());
        for (final StepExecution execution : executions) {
            delegates.add(new DelegateStepExecutionImpl(execution, repository));
        }
        return delegates;
    }

}
