package io.machinecode.chainlink.core.management;

import gnu.trove.set.hash.THashSet;
import io.machinecode.chainlink.core.context.ExecutionContextImpl;
import io.machinecode.chainlink.core.context.JobContextImpl;
import io.machinecode.chainlink.core.element.JobImpl;
import io.machinecode.chainlink.core.factory.JobFactory;
import io.machinecode.chainlink.core.registry.LocalRegistry;
import io.machinecode.chainlink.core.util.PropertiesConverter;
import io.machinecode.chainlink.core.util.Repository;
import io.machinecode.chainlink.core.work.JobExecutable;
import io.machinecode.chainlink.core.repository.DelegateJobExecution;
import io.machinecode.chainlink.core.repository.DelegateStepExecution;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.element.Job;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.chainlink.spi.security.Security;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.work.JobWork;
import io.machinecode.then.api.Promise;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JobOperatorImpl implements ExtendedJobOperator {

    private static final Logger log = Logger.getLogger(JobOperatorImpl.class);

    protected final Configuration configuration;
    protected final Executor executor;
    protected final Transport<?> transport;
    protected final Registry registry;
    protected final Security security;
    protected final ExecutionRepositoryId executionRepositoryId;

    public JobOperatorImpl(final Configuration configuration) {
        this.configuration = configuration;
        this.executor = configuration.getExecutor();
        this.transport = configuration.getTransport();
        this.registry = configuration.getRegistry();
        this.security = this.configuration.getSecurity();
        this.executionRepositoryId = this.registry.registerExecutionRepository(
                transport.generateExecutionRepositoryId(),
                configuration.getExecutionRepository()
        );
    }

    @Override
    public void open(final Configuration configuration) throws Exception {
        this.transport.open(configuration);
        this.registry.open(configuration);
        this.executor.open(configuration);
    }

    @Override
    public void close() throws Exception {
        this.transport.close();
        this.registry.unregisterExecutionRepository(this.executionRepositoryId);
        this.registry.close();
        this.executor.close();
    }

    @Override
    public Set<String> getJobNames() throws JobSecurityException {
        try {
            final Set<String> jobNames = registry.getExecutionRepository(this.executionRepositoryId).getJobNames();
            final Set<String> copy = new THashSet<>(jobNames.size());
            for (final String jobName : jobNames) {
                if (!this.security.filterJobName(jobName)) {
                    copy.add(jobName);
                }
            }
            return copy;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public int getJobInstanceCount(final String jobName) throws NoSuchJobException, JobSecurityException {
        try {
            this.security.canAccessJob(jobName);
            return registry.getExecutionRepository(this.executionRepositoryId).getJobInstanceCount(jobName); //TODO This needs to fetch a list of id's that we can then filter on
        } catch (final NoSuchJobException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public ExtendedJobInstance getJobInstanceById(final long jobInstanceId) throws NoSuchJobInstanceException, JobSecurityException {
        try {
            this.security.canAccessJobInstance(jobInstanceId);
            return registry.getExecutionRepository(this.executionRepositoryId).getJobInstance(jobInstanceId);
        } catch (final NoSuchJobInstanceException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws NoSuchJobException, JobSecurityException {
        try {
            security.canAccessJob(jobName);
            final List<JobInstance> jobInstances =  registry.getExecutionRepository(this.executionRepositoryId).getJobInstances(jobName, start, count);
            final ArrayList<JobInstance> copy = new ArrayList<>(jobInstances.size());
            for (final JobInstance jobInstance : jobInstances) {
                if (!security.filterJobInstance(jobInstance.getInstanceId())) {
                    copy.add(jobInstance);
                }
            }
            return copy;
        } catch (final NoSuchJobException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<Long> getRunningExecutions(final String jobName) throws NoSuchJobException, JobSecurityException {
        try {
            final List<Long> jobExecutionIds = registry.getExecutionRepository(this.executionRepositoryId).getRunningExecutions(jobName); //TODO This should probably go through Registry
            final ArrayList<Long> copy = new ArrayList<>(jobExecutionIds.size());
            for (final Long jobExecutionId : jobExecutionIds) {
                if (!security.filterJobExecution(jobExecutionId)) {
                    copy.add(jobExecutionId);
                }
            }
            return copy;
        } catch (final NoSuchJobException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Properties getParameters(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        try {
            this.security.canAccessJobExecution(jobExecutionId);
            return registry.getExecutionRepository(this.executionRepositoryId).getParameters(jobExecutionId);
        } catch (final NoSuchJobExecutionException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public long start(final String jslName, final Properties parameters) throws JobStartException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001200.operator.start"), jslName);
        this.security.canStartJob(jslName);
        try {
            final io.machinecode.chainlink.spi.element.Job theirs = configuration.getJobLoader().load(jslName);
            final JobImpl job = JobFactory.produce(theirs, parameters);
            return _startJob(job, jslName, parameters).getJobExecutionId();
        } catch (final JobStartException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    @Override
    public JobOperationImpl startJob(final String jslName, final Properties parameters) throws JobStartException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001200.operator.start"), jslName);
        this.security.canStartJob(jslName);
        try {
            final io.machinecode.chainlink.spi.element.Job theirs = configuration.getJobLoader().load(jslName);
            final JobImpl job = JobFactory.produce(theirs, parameters);
            return _startJob(job, jslName, parameters);
        } catch (final JobStartException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    public JobOperationImpl startJob(final Job theirs, final String jslName, final Properties parameters) throws JobStartException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001200.operator.start"), jslName);
        this.security.canStartJob(jslName);
        try {
            final JobImpl job = JobFactory.produce(theirs, parameters);
            return _startJob(job, jslName, parameters);
        } catch (final JobStartException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    private JobOperationImpl _startJob(final JobWork job, final String jslName, final Properties parameters) throws Exception {
        JobFactory.validate(job);

        final ExecutionRepository repository = repository();
        final ExtendedJobInstance instance = repository.createJobInstance(job.getId(), jslName, new Date());
        final ExtendedJobExecution execution = repository.createJobExecution(instance.getInstanceId(), job.getId(), parameters, new Date());
        final long jobExecutionId = execution.getExecutionId();
        final ExecutionContext context = new ExecutionContextImpl(
                job,
                new JobContextImpl(instance, execution, PropertiesConverter.convert(job.getProperties())),
                null,
                jobExecutionId,
                null,
                null,
                null
        );
        final Promise<?,?,?> promise = executor.execute(jobExecutionId, new JobExecutable(
                null,
                this.executionRepositoryId,
                job,
                context
        ));
        log.tracef(Messages.get("CHAINLINK-001300.operator.started"), jobExecutionId, job.getId());
        return new JobOperationImpl(
                jobExecutionId,
                promise,
                repository
        );
    }

    @Override
    public JobOperationImpl getJobOperation(final long jobExecutionId) throws JobExecutionNotRunningException {
        this.security.canAccessJobExecution(jobExecutionId);
        try {
            final Promise<?,?,?> promise = registry.getJob(jobExecutionId);
            return new JobOperationImpl(
                    jobExecutionId,
                    promise,
                    registry.getExecutionRepository(this.executionRepositoryId)
            );
        } catch (final JobExecutionNotRunningException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobRestartException(e);
        }
    }

    @Override
    public long restart(final long jobExecutionId, final Properties parameters) throws JobExecutionAlreadyCompleteException, NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobRestartException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001201.operator.restart"), jobExecutionId);
        this.security.canRestartJob(jobExecutionId);
        try {
            final ExecutionRepository repository = repository();
            final ExtendedJobInstance instance = repository.getJobInstanceForExecution(jobExecutionId);
            final io.machinecode.chainlink.spi.element.Job theirs = configuration.getJobLoader().load(instance.getJslName());
            final JobImpl job = JobFactory.produce(theirs, parameters);
            return _restart(job, jobExecutionId, instance, parameters).getJobExecutionId();
        } catch (final JobExecutionAlreadyCompleteException | NoSuchJobExecutionException
                | JobExecutionNotMostRecentException | JobRestartException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobRestartException(e);
        }
    }

    @Override
    public JobOperationImpl restartJob(final long jobExecutionId, final Properties parameters) throws JobExecutionAlreadyCompleteException, NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobRestartException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001201.operator.restart"), jobExecutionId);
        this.security.canRestartJob(jobExecutionId);
        try {
            final ExecutionRepository repository = repository();
            final ExtendedJobInstance instance = repository.getJobInstanceForExecution(jobExecutionId);
            final io.machinecode.chainlink.spi.element.Job theirs = configuration.getJobLoader().load(instance.getJslName());
            final JobImpl job = JobFactory.produce(theirs, parameters);
            return _restart(job, jobExecutionId, instance, parameters);
        } catch (final JobExecutionAlreadyCompleteException | NoSuchJobExecutionException
                | JobExecutionNotMostRecentException | JobRestartException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobRestartException(e);
        }
    }

    private JobOperationImpl _restart(final JobWork job, final long jobExecutionId, final JobInstance instance, final Properties parameters) throws Exception {
        JobFactory.validate(job);
        final ExecutionRepository repository = repository();
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
                execution.getExecutionId(),
                lastExecution.getExecutionId(),
                lastExecution.getRestartElementId(),
                null
        );
        final Promise<?,?,?> promise = executor.execute(restartExecutionId, new JobExecutable(
                null,
                this.executionRepositoryId,
                job,
                context
        ));
        log.tracef(Messages.get("CHAINLINK-001301.operator.restarted"), jobExecutionId, job.getId());
        return new JobOperationImpl(
                restartExecutionId,
                promise,
                repository
        );
    }

    @Override
    public void stop(final long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException, JobSecurityException {
        this.security.canStopJob(jobExecutionId);
        stopJob(jobExecutionId);
    }

    @Override
    public Promise<?,Throwable,?> stopJob(final long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001202.operator.stop"), jobExecutionId);
        this.security.canRestartJob(jobExecutionId);
        try {
            final ExecutionRepository repository = repository();
            final ExtendedJobExecution execution = repository.getJobExecution(jobExecutionId); //This will throw a NoSuchJobExecutionException if required
            final Promise<?,Throwable,?> promise = registry.getJob(jobExecutionId);
            if (promise == null) {
                throw new JobExecutionNotRunningException(Messages.format("CHAINLINK-001002.operator.not.running", jobExecutionId));
            }
            executor.cancel(promise);
            log.tracef(Messages.get("CHAINLINK-001302.operator.stopped"), jobExecutionId, execution.getJobName());
            return promise;
        } catch (final NoSuchJobExecutionException | JobExecutionNotRunningException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    @Override
    public void abandon(final long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionIsRunningException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001203.operator.abandon"), jobExecutionId);
        this.security.canAbandonJob(jobExecutionId);
        try {
            try {
                //TODO WHat should happen here if this is called on a node that didn't originate the job?
                registry.getJob(jobExecutionId);
                throw new JobExecutionIsRunningException(Messages.format("CHAINLINK-001001.operator.running", jobExecutionId));
            } catch (final JobExecutionNotRunningException e) {
                Repository.abandonedJob(registry.getExecutionRepository(this.executionRepositoryId), jobExecutionId);
                log.tracef(Messages.get("CHAINLINK-001303.operator.abandoned"), jobExecutionId);
            }
        } catch (final NoSuchJobExecutionException | JobExecutionIsRunningException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    @Override
    public ExtendedJobInstance getJobInstance(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        this.security.canAccessJobExecution(jobExecutionId);
        try {
        final ExtendedJobInstance jobInstance = registry.getExecutionRepository(this.executionRepositoryId).getJobInstanceForExecution(jobExecutionId);
            this.security.canAccessJobInstance(jobInstance.getInstanceId());
            return jobInstance;
        } catch (final NoSuchJobExecutionException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    @Override
    public List<JobExecution> getJobExecutions(final JobInstance instance) throws NoSuchJobInstanceException, JobSecurityException {
        this.security.canAccessJobInstance(instance.getInstanceId());
        try {
            final ExecutionRepository repository = repository();
            final List<? extends JobExecution> jobExecutions = repository.getJobExecutions(instance.getInstanceId());
            final List<JobExecution> delegates = new ArrayList<>(jobExecutions.size());
            for (final JobExecution jobExecution : jobExecutions) {
                if (!this.security.filterJobExecution(jobExecution.getExecutionId())) {
                    delegates.add(new DelegateJobExecution(jobExecution, repository));
                }
            }
            return delegates;
        } catch (final NoSuchJobInstanceException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    @Override
    public ExtendedJobExecution getJobExecution(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        this.security.canAccessJobExecution(jobExecutionId);
        try {
            final ExecutionRepository repository = repository();
            return new DelegateJobExecution(repository.getJobExecution(jobExecutionId), repository);
        } catch (final NoSuchJobExecutionException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    @Override
    public List<StepExecution> getStepExecutions(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        this.security.canAccessJobExecution(jobExecutionId);
        try {
            final ExecutionRepository repository = repository();
            final List<? extends StepExecution> stepExecutions =  repository.getStepExecutionsForJobExecution(jobExecutionId);
            final List<StepExecution> delegates = new ArrayList<>(stepExecutions.size());
            for (final StepExecution stepExecution : stepExecutions) {
                if (!this.security.filterStepExecution(stepExecution.getStepExecutionId())) {
                    delegates.add(new DelegateStepExecution(stepExecution, repository));
                }
            }
            return delegates;
        } catch (final NoSuchJobExecutionException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    protected ExecutionRepository repository() {
        final ExecutionRepository repository = registry.getExecutionRepository(this.executionRepositoryId);
        LocalRegistry.assertExecutionRepository(repository, executionRepositoryId);
        return repository;
    }
}
