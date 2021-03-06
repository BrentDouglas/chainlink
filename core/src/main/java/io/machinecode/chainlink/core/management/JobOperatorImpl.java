/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.core.management;

import gnu.trove.set.hash.THashSet;
import io.machinecode.chainlink.core.context.ExecutionContextImpl;
import io.machinecode.chainlink.core.context.JobContextImpl;
import io.machinecode.chainlink.core.execution.ExecutableEventImpl;
import io.machinecode.chainlink.core.factory.JobFactory;
import io.machinecode.chainlink.core.jsl.impl.JobImpl;
import io.machinecode.chainlink.core.property.SystemPropertyLookup;
import io.machinecode.chainlink.core.registry.UUIDId;
import io.machinecode.chainlink.core.repository.DelegateJobExecution;
import io.machinecode.chainlink.core.repository.DelegateStepExecution;
import io.machinecode.chainlink.core.then.ChainImpl;
import io.machinecode.chainlink.core.util.PropertiesConverter;
import io.machinecode.chainlink.core.util.Repo;
import io.machinecode.chainlink.core.work.JobExecutable;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.jsl.Job;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.registry.RepositoryId;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.chainlink.spi.repository.Repository;
import io.machinecode.chainlink.spi.security.Security;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.then.api.Promise;
import org.jboss.logging.Logger;

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
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.StepExecution;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JobOperatorImpl implements ExtendedJobOperator {

    private static final Logger log = Logger.getLogger(JobOperatorImpl.class);

    protected final Configuration configuration;
    protected final Executor executor;
    protected final Repository repository;
    protected final Transport transport;
    protected final Registry registry;
    protected final Security security;
    protected final RepositoryId repositoryId;
    protected final SystemPropertyLookup properties;

    protected final ExecutorService cancellation;

    public JobOperatorImpl(final Configuration configuration) {
        this(configuration, SystemPropertyLookup.INSTANCE);
    }

    public JobOperatorImpl(final Configuration configuration, final SystemPropertyLookup properties) {
        this.configuration = configuration;
        this.executor = configuration.getExecutor();
        this.transport = configuration.getTransport();
        this.registry = configuration.getRegistry();
        this.security = this.configuration.getSecurity();
        this.repositoryId = new UUIDId(transport);
        this.repository = configuration.getRepository();
        this.registry.registerRepository(
                this.repositoryId,
                this.repository
        );
        this.cancellation = Executors.newSingleThreadExecutor();
        this.properties = properties;
    }


    @Override
    public void open(final Configuration configuration) throws Exception {
        this.transport.open(configuration);
        this.registry.open(configuration);
        this.executor.open(configuration);
        this.security.open(configuration);
    }

    @Override
    public void close() throws Exception {
        Exception exception = null;
        try {
            this.security.close();
        } catch (final Exception e) {
            exception = e;
        }
        try {
            this.executor.close();
        } catch (final Exception e) {
            if (exception == null) {
                exception = e;
            } else {
                exception.addSuppressed(e);
            }
        }
        try {
            this.registry.unregisterRepository(this.repositoryId);
            this.registry.close();
        } catch (final Exception e) {
            if (exception == null) {
                exception = e;
            } else {
                exception.addSuppressed(e);
            }
        }
        try {
            this.transport.close();
        } catch (final Exception e) {
            if (exception == null) {
                exception = e;
            } else {
                exception.addSuppressed(e);
            }
        }
        try {
            this.cancellation.shutdown();
        } catch (final Exception e) {
            if (exception == null) {
                exception = e;
            } else {
                exception.addSuppressed(e);
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

    @Override
    public Set<String> getJobNames() throws JobSecurityException {
        try {
            final Set<String> jobNames = repository.getJobNames();
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
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public int getJobInstanceCount(final String jobName) throws NoSuchJobException, JobSecurityException {
        try {
            this.security.canAccessJob(jobName);
            return repository.getJobInstanceCount(jobName); //TODO This needs to fetch a list of id's that we can then filter on
        } catch (final NoSuchJobException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public ExtendedJobInstance getJobInstanceById(final long jobInstanceId) throws NoSuchJobInstanceException, JobSecurityException {
        try {
            this.security.canAccessJobInstance(jobInstanceId);
            return repository.getJobInstance(jobInstanceId);
        } catch (final NoSuchJobInstanceException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws NoSuchJobException, JobSecurityException {
        try {
            security.canAccessJob(jobName);
            final List<JobInstance> jobInstances =  repository.getJobInstances(jobName, start, count);
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
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public List<Long> getRunningExecutions(final String jobName) throws NoSuchJobException, JobSecurityException {
        try {
            security.canAccessJob(jobName);
            final List<Long> jobExecutionIds = repository.getRunningExecutions(jobName); //TODO This should probably go through Registry
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
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public Properties getParameters(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        try {
            this.security.canAccessJobExecution(jobExecutionId);
            return repository.getParameters(jobExecutionId);
        } catch (final NoSuchJobExecutionException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public long start(final String jslName, final Properties parameters) throws JobStartException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001200.operator.start"), jslName);
        try {
            this.security.canStartJob(jslName);
            final Job theirs = configuration.getJobLoader().load(jslName);
            final JobImpl job = JobFactory.produce(theirs, parameters, properties);
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
        try {
            this.security.canStartJob(jslName);
            final Job theirs = configuration.getJobLoader().load(jslName);
            final JobImpl job = JobFactory.produce(theirs, parameters, properties);
            return _startJob(job, jslName, parameters);
        } catch (final JobStartException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    public JobOperationImpl startJob(final Job theirs, final String jslName, final Properties parameters) throws JobStartException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001200.operator.start"), jslName);
        try {
            this.security.canStartJob(jslName);
            final JobImpl job = JobFactory.produce(theirs, parameters, properties);
            return _startJob(job, jslName, parameters);
        } catch (final JobStartException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    private JobOperationImpl _startJob(final JobImpl job, final String jslName, final Properties parameters) throws Exception {
        final ExtendedJobInstance instance = repository.createJobInstance(job.getId(), jslName, new Date());
        final ExtendedJobExecution execution = repository.createJobExecution(instance.getInstanceId(), job.getId(), parameters, new Date());
        final long jobExecutionId = execution.getExecutionId();
        final ExecutionContextImpl context = new ExecutionContextImpl(
                new JobContextImpl(instance, execution, PropertiesConverter.convert(job.getProperties())),
                null,
                jobExecutionId,
                null,
                null,
                null
        );
        final Chain<?> chain = new ChainImpl<Void>();
        final ChainId chainId = new UUIDId(transport);
        registry.registerJob(jobExecutionId, chainId, chain);
        executor.getWorker().execute(new ExecutableEventImpl(new JobExecutable(
                null,
                this.repositoryId,
                job,
                context
        ), chainId));
        log.tracef(Messages.get("CHAINLINK-001300.operator.started"), jobExecutionId, job.getId());
        return new JobOperationImpl(
                jobExecutionId,
                chain,
                repository
        );
    }

    @Override
    public JobOperationImpl getJobOperation(final long jobExecutionId) throws JobExecutionNotRunningException {
        try {
            this.security.canAccessJobExecution(jobExecutionId);
            final Chain<?> job = registry.getJob(jobExecutionId);
            if (job == null) {
                throw new JobExecutionNotRunningException(Messages.format("CHAINLINK-001002.operator.not.running", jobExecutionId));
            }
            return new JobOperationImpl(
                    jobExecutionId,
                    job,
                    repository
            );
        } catch (final JobExecutionNotRunningException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public long restart(final long jobExecutionId, final Properties parameters) throws JobExecutionAlreadyCompleteException, NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobRestartException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001201.operator.restart"), jobExecutionId);
        try {
            this.security.canRestartJob(jobExecutionId);
            final ExtendedJobInstance instance = repository.getJobInstanceForExecution(jobExecutionId);
            final Job theirs = configuration.getJobLoader().load(instance.getJslName());
            final JobImpl job = JobFactory.produce(theirs, parameters, properties);
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
        try {
            this.security.canRestartJob(jobExecutionId);
            final ExtendedJobInstance instance = repository.getJobInstanceForExecution(jobExecutionId);
            final Job theirs = configuration.getJobLoader().load(instance.getJslName());
            final JobImpl job = JobFactory.produce(theirs, parameters, properties);
            return _restart(job, jobExecutionId, instance, parameters);
        } catch (final JobExecutionAlreadyCompleteException | NoSuchJobExecutionException
                | JobExecutionNotMostRecentException | JobRestartException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobRestartException(e);
        }
    }

    private JobOperationImpl _restart(final JobImpl job, final long jobExecutionId, final JobInstance instance, final Properties parameters) throws Exception {
        if (!Boolean.parseBoolean(job.getRestartable())) {
            throw new JobRestartException(Messages.format("CHAINLINK-001100.operator.cant.restart.job", jobExecutionId, job.getId(), jobExecutionId));
        }
        final ExtendedJobExecution lastExecution = repository.getJobExecution(jobExecutionId);
        final ExtendedJobExecution execution = repository.restartJobExecution(jobExecutionId, parameters);
        final long restartExecutionId = execution.getExecutionId();
        final ExecutionContextImpl context = new ExecutionContextImpl(
                new JobContextImpl(instance, execution, PropertiesConverter.convert(job.getProperties())),
                null,
                execution.getExecutionId(),
                lastExecution.getExecutionId(),
                lastExecution.getRestartElementId(),
                null
        );
        final Chain<?> chain = new ChainImpl<Void>();
        final ChainId chainId = new UUIDId(transport);
        registry.registerJob(restartExecutionId, chainId, chain);
        executor.getWorker().execute(new ExecutableEventImpl(new JobExecutable(
                null,
                this.repositoryId,
                job,
                context
        ), chainId));
        log.tracef(Messages.get("CHAINLINK-001301.operator.restarted"), jobExecutionId, job.getId());
        return new JobOperationImpl(
                restartExecutionId,
                chain,
                repository
        );
    }

    @Override
    public void stop(final long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException, JobSecurityException {
        stopJob(jobExecutionId);
    }

    @Override
    public Promise<?,Throwable,?> stopJob(final long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001202.operator.stop"), jobExecutionId);
        try {
            this.security.canStopJob(jobExecutionId);
            //This will throw a NoSuchJobExecutionException if required
            final ExtendedJobExecution execution = repository.getJobExecution(jobExecutionId);
            final Chain<?> job = registry.getJob(jobExecutionId);
            if (job == null) {
                throw new JobExecutionNotRunningException(Messages.format("CHAINLINK-001002.operator.not.running", jobExecutionId));
            }
            cancellation.execute(new Cancel(job));
            log.tracef(Messages.get("CHAINLINK-001302.operator.stopped"), jobExecutionId, execution.getJobName());
            return job;
        } catch (final NoSuchJobExecutionException | JobExecutionNotRunningException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public void abandon(final long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionIsRunningException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001203.operator.abandon"), jobExecutionId);
        try {
            this.security.canAbandonJob(jobExecutionId);
            //TODO WHat should happen here if this is called on a node that didn't originate the job?
            final Chain<?> job = registry.getJob(jobExecutionId);
            if (job != null) {
                throw new JobExecutionIsRunningException(Messages.format("CHAINLINK-001001.operator.running", jobExecutionId));
            }
            Repo.updateJob(repository, jobExecutionId, BatchStatus.ABANDONED);
            log.tracef(Messages.get("CHAINLINK-001303.operator.abandoned"), jobExecutionId);
        } catch (final NoSuchJobExecutionException | JobExecutionIsRunningException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public ExtendedJobInstance getJobInstance(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        try {
            this.security.canAccessJobExecution(jobExecutionId); //TODO Maybe?
            final ExtendedJobInstance jobInstance = repository.getJobInstanceForExecution(jobExecutionId);
            this.security.canAccessJobInstance(jobInstance.getInstanceId());
            return jobInstance;
        } catch (final NoSuchJobExecutionException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public List<JobExecution> getJobExecutions(final JobInstance instance) throws NoSuchJobInstanceException, JobSecurityException {
        try {
            this.security.canAccessJobInstance(instance.getInstanceId());
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
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public ExtendedJobExecution getJobExecution(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        try {
            this.security.canAccessJobExecution(jobExecutionId);
            return new DelegateJobExecution(repository.getJobExecution(jobExecutionId), repository);
        } catch (final NoSuchJobExecutionException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public List<StepExecution> getStepExecutions(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        try {
            this.security.canAccessJobExecution(jobExecutionId);
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
            throw new BatchRuntimeException(e);
        }
    }

    private static class Cancel implements Runnable {
        private final Promise<?, Throwable, ?> promise;

        public Cancel(final Promise<?, Throwable, ?> promise) {
            this.promise = promise;
        }

        @Override
        public void run() {
            promise.cancel(true);
        }
    }
}
