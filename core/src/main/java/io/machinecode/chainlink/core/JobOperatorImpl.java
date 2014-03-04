package io.machinecode.chainlink.core;

import io.machinecode.chainlink.core.configuration.ConfigurationFactoryImpl;
import io.machinecode.chainlink.core.configuration.RuntimeConfigurationImpl;
import io.machinecode.chainlink.core.exec.EventedExecutorFactory;
import io.machinecode.chainlink.core.factory.JobFactory;
import io.machinecode.chainlink.core.impl.ExecutionContextImpl;
import io.machinecode.chainlink.core.impl.DelegateJobExecutionImpl;
import io.machinecode.chainlink.core.impl.DelegateStepExecutionImpl;
import io.machinecode.chainlink.core.impl.JobContextImpl;
import io.machinecode.chainlink.core.model.JobImpl;
import io.machinecode.chainlink.core.util.PropertiesConverter;
import io.machinecode.chainlink.core.util.ResolvableService;
import io.machinecode.chainlink.core.work.JobExecutable;
import io.machinecode.chainlink.core.work.Repository;
import io.machinecode.chainlink.spi.ExecutionRepository;
import io.machinecode.chainlink.spi.ExtendedJobExecution;
import io.machinecode.chainlink.spi.ExtendedJobInstance;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.execution.ExecutorFactory;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.work.JobWork;
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
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.StepExecution;
import java.util.ArrayList;
import java.util.Date;
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
        executor = executorFactory.produce(configuration, 8); //TODO
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
    public long start(final String jslName, final Properties parameters) throws JobStartException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001200.operator.start"), jslName);
        try {
            final io.machinecode.chainlink.spi.element.Job theirs = configuration.getJobLoader().load(jslName);
            final JobImpl job = JobFactory.INSTANCE.produceExecution(theirs, parameters);

            return startJob(job, jslName, parameters).getJobExecutionId();
        } catch (final JobStartException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    public JobOperationImpl startJob(final JobWork job, final String jslName, final Properties parameters) throws Exception {
        JobFactory.INSTANCE.validate(job);

        final ExecutionRepository repository = executor.getRepository();
        final ExtendedJobInstance instance = repository.createJobInstance(job, jslName);
        final ExtendedJobExecution execution = repository.createJobExecution(instance, parameters, new Date());
        final long jobExecutionId = execution.getExecutionId();
        final ExecutionContext context = new ExecutionContextImpl(
                job,
                new JobContextImpl(instance, execution, PropertiesConverter.convert(job.getProperties())),
                null,
                execution,
                null,
                null
        );
        final Deferred<?> deferred = executor.execute(jobExecutionId, new JobExecutable(null, job, context));
        return new JobOperationImpl(
                jobExecutionId,
                deferred,
                repository
        );
    }

    public JobOperationImpl getJobOperation(final long jobExecutionId) throws JobExecutionNotRunningException {
        final Deferred<?> deferred = executor.getJob(jobExecutionId);
        return new JobOperationImpl(
                jobExecutionId,
                deferred,
                executor.getRepository()
        );
    }

    @Override
    public long restart(final long jobExecutionId, final Properties parameters) throws JobExecutionAlreadyCompleteException, NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobRestartException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001201.operator.restart"), jobExecutionId);
        try {
            final ExecutionRepository repository = executor.getRepository();
            final ExtendedJobInstance instance = repository.getJobInstanceForExecution(jobExecutionId);
            final io.machinecode.chainlink.spi.element.Job theirs = configuration.getJobLoader().load(instance.getJslName());
            final JobImpl job = JobFactory.INSTANCE.produceExecution(theirs, parameters);
            return _restart(job, jobExecutionId, instance, parameters).getJobExecutionId();
        } catch (final JobExecutionAlreadyCompleteException e) {
            throw e;
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobExecutionNotMostRecentException e) {
            throw e;
        } catch (final JobRestartException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobRestartException(e);
        }
    }

    public JobOperationImpl restartJob(final long jobExecutionId, final Properties parameters) throws JobExecutionAlreadyCompleteException, NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobRestartException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001201.operator.restart"), jobExecutionId);
        try {
            final ExecutionRepository repository = executor.getRepository();
            final ExtendedJobInstance instance = repository.getJobInstanceForExecution(jobExecutionId);
            final io.machinecode.chainlink.spi.element.Job theirs = configuration.getJobLoader().load(instance.getJslName());
            final JobImpl job = JobFactory.INSTANCE.produceExecution(theirs, parameters);
            return _restart(job, jobExecutionId, instance, parameters);
        } catch (final JobExecutionAlreadyCompleteException e) {
            throw e;
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobExecutionNotMostRecentException e) {
            throw e;
        } catch (final JobRestartException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobRestartException(e);
        }
    }

    private JobOperationImpl _restart(final JobWork job, final long jobExecutionId, final JobInstance instance, final Properties parameters) throws JobExecutionAlreadyCompleteException, NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobRestartException, JobSecurityException {
        JobFactory.INSTANCE.validate(job);
        final ExecutionRepository repository = executor.getRepository();
        final ExtendedJobExecution lastExecution = repository.getJobExecution(jobExecutionId);
        final ExtendedJobExecution execution = repository.restartJobExecution(jobExecutionId, parameters);
        final long restartExecutionId = execution.getExecutionId();
        if (!job.isRestartable()) {
            throw new JobRestartException(Messages.format("CHAINLINK-001100.operator.cant.restart.job", jobExecutionId, job.getId(), restartExecutionId));
        }
        final ExecutionContext context = new ExecutionContextImpl(
                job,
                new JobContextImpl(instance, execution, PropertiesConverter.convert(job.getProperties())),
                null,
                execution,
                lastExecution,
                null
        );
        final Deferred<?> deferred = executor.execute(restartExecutionId, new JobExecutable(null, job, context));
        return new JobOperationImpl(
                restartExecutionId,
                deferred,
                repository
        );
    }

    @Override
    public void stop(final long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException, JobSecurityException {
        stopJob(jobExecutionId);
    }

    public Deferred<?> stopJob(final long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001202.operator.stop"), jobExecutionId);
        final ExecutionRepository repository = executor.getRepository();
        repository.getJobExecution(jobExecutionId); //This will throw a NoSuchJobExecutionException if required
        final Deferred<?> deferred = executor.getJob(jobExecutionId);
        if (deferred == null) {
            throw new JobExecutionNotRunningException(Messages.format("CHAINLINK-001002.operator.not.running", jobExecutionId));
        }
        executor.cancel(deferred);
        return deferred;
    }

    @Override
    public void abandon(final long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionIsRunningException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001203.operator.abandon"), jobExecutionId);
        try {
            executor.getJob(jobExecutionId);
            throw new JobExecutionIsRunningException(Messages.format("CHAINLINK-001001.operator.running", jobExecutionId));
        } catch (final JobExecutionNotRunningException e) {
            Repository.abandonedJob(executor.getRepository(), jobExecutionId);
        }
    }

    @Override
    public JobInstance getJobInstance(final long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        return executor.getRepository().getJobInstanceForExecution(executionId);
    }

    @Override
    public List<JobExecution> getJobExecutions(final JobInstance instance) throws NoSuchJobInstanceException, JobSecurityException {
        final ExecutionRepository repository = executor.getRepository();
        final List<? extends JobExecution> executions =  repository.getJobExecutions(instance);
        final List<JobExecution> delegates = new ArrayList<JobExecution>(executions.size());
        for (final JobExecution execution : executions) {
            delegates.add(new DelegateJobExecutionImpl(execution, repository));
        }
        return delegates;
    }

    @Override
    public JobExecution getJobExecution(final long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        final ExecutionRepository repository = executor.getRepository();
        return new DelegateJobExecutionImpl(repository.getJobExecution(executionId), repository);
    }

    @Override
    public List<StepExecution> getStepExecutions(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        final ExecutionRepository repository = executor.getRepository();
        final List<? extends StepExecution> executions =  repository.getStepExecutionsForJob(jobExecutionId);
        final List<StepExecution> delegates = new ArrayList<StepExecution>(executions.size());
        for (final StepExecution execution : executions) {
            delegates.add(new DelegateStepExecutionImpl(execution, repository));
        }
        return delegates;
    }

}
