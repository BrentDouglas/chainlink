package io.machinecode.chainlink.core.management;

import gnu.trove.set.hash.THashSet;
import io.machinecode.chainlink.repository.core.DelegateJobExecution;
import io.machinecode.chainlink.repository.core.DelegateStepExecution;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.core.factory.JobFactory;
import io.machinecode.chainlink.core.context.ExecutionContextImpl;
import io.machinecode.chainlink.core.context.JobContextImpl;
import io.machinecode.chainlink.core.element.JobImpl;
import io.machinecode.chainlink.core.management.jmx.JmxOperator;
import io.machinecode.chainlink.core.util.PropertiesConverter;
import io.machinecode.chainlink.core.work.JobExecutable;
import io.machinecode.chainlink.core.util.Repository;
import io.machinecode.chainlink.spi.Lifecycle;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.factory.WorkerFactory;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import io.machinecode.chainlink.spi.transport.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.security.SecurityCheck;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.work.JobWork;
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
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobOperatorImpl implements ExtendedJobOperator, Lifecycle {

    private static final Logger log = Logger.getLogger(JobOperatorImpl.class);

    private final Configuration configuration;
    private final Executor executor;
    private final Transport transport;
    private final SecurityCheck securityCheck;
    private final ExecutionRepositoryId executionRepositoryId;
    private ObjectName jmxName;

    public JobOperatorImpl(final Configuration configuration) {
        this.configuration = configuration;
        this.executor = configuration.getExecutor();
        this.transport = configuration.getTransport();
        this.securityCheck = this.configuration.getSecurityCheck();
        this.executor.startup();
        this.transport.startup();
        this.executionRepositoryId = this.transport.generateExecutionRepositoryId(configuration.getRepository());
        this.transport.registerExecutionRepository(this.executionRepositoryId, configuration.getRepository());
    }

    @Override
    public void startup() {
        final int numThreads = Integer.parseInt(configuration.getProperty(Constants.THREAD_POOL_SIZE));
        final WorkerFactory workerFactory = configuration.getWorkerFactory();
        for (int i = 0; i < numThreads; ++i) {
            final Worker worker;
            try {
                worker = workerFactory.produce(configuration);
            } catch (final Exception e) {
                throw new RuntimeException(e); //TODO
            }
            worker.startup();
            this.transport.registerWorker(worker.getWorkerId(), worker);
        }
        final String domain = ObjectName.quote(configuration.getProperty(Constants.JMX_DOMAIN, Constants.Defaults.JMX_DOMAIN));
        final MBeanServer server = configuration.getMBeanServer();
        if (server != null) {
            try {
                jmxName = new ObjectName(_objectName(domain));
                if (!server.isRegistered(jmxName)) {
                    server.registerMBean(new JmxOperator(this, server, domain), jmxName);
                }
            } catch (final Exception e) {
                throw new RuntimeException(e); //TODO Message
            }
        }
    }

    private String _objectName(final String domain) {
        final StringBuilder builder = new StringBuilder();
        builder.append(domain)
                .append(":type=Operator,name=JmxOperator");
        //TODO This needs to bung on stuff about transport, repo, etc
        return builder.toString();
    }

    @Override
    public void shutdown() {
        final MBeanServer server = configuration.getMBeanServer();
        if (server != null) {
            try {
                if (server.isRegistered(jmxName)) {
                    server.unregisterMBean(jmxName);
                }
            } catch (final Exception e) {
                throw new RuntimeException(e); //TODO Message
            }
        }
        this.transport.unregisterExecutionRepository(this.executionRepositoryId);
        this.transport.shutdown();
        this.executor.shutdown();
    }

    @Override
    public Set<String> getJobNames() throws JobSecurityException {
        try {
            final Set<String> jobNames = transport.getExecutionRepository(this.executionRepositoryId).getJobNames();
            final Set<String> copy = new THashSet<String>(jobNames.size());
            for (final String jobName : jobNames) {
                if (!this.securityCheck.filterJobName(jobName)) {
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
            this.securityCheck.canAccessJob(jobName);
            return transport.getExecutionRepository(this.executionRepositoryId).getJobInstanceCount(jobName); //TODO This needs to fetch a list of id's that we can then filter on
        } catch (final NoSuchJobException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public ExtendedJobInstance getJobInstanceById(final long jobInstanceId) throws NoSuchJobInstanceException, JobSecurityException {
        try {
            this.securityCheck.canAccessJobInstance(jobInstanceId);
            return transport.getExecutionRepository(this.executionRepositoryId).getJobInstance(jobInstanceId);
        } catch (final NoSuchJobInstanceException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws NoSuchJobException, JobSecurityException {
        try {
            securityCheck.canAccessJob(jobName);
            final List<JobInstance> jobInstances =  transport.getExecutionRepository(this.executionRepositoryId).getJobInstances(jobName, start, count);
            final ArrayList<JobInstance> copy = new ArrayList<JobInstance>(jobInstances.size());
            for (final JobInstance jobInstance : jobInstances) {
                if (!securityCheck.filterJobInstance(jobInstance.getInstanceId())) {
                    copy.add(jobInstance);
                }
            }
            return copy;
        } catch (final NoSuchJobException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<Long> getRunningExecutions(final String jobName) throws NoSuchJobException, JobSecurityException {
        try {
            final List<Long> jobExecutionIds = transport.getExecutionRepository(this.executionRepositoryId).getRunningExecutions(jobName); //TODO This should probably go through Transport
            final ArrayList<Long> copy = new ArrayList<Long>(jobExecutionIds.size());
            for (final Long jobExecutionId : jobExecutionIds) {
                if (!securityCheck.filterJobExecution(jobExecutionId)) {
                    copy.add(jobExecutionId);
                }
            }
            return copy;
        } catch (final NoSuchJobException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Properties getParameters(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        try {
            this.securityCheck.canAccessJobExecution(jobExecutionId);
            return transport.getExecutionRepository(this.executionRepositoryId).getParameters(jobExecutionId);
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public long start(final String jslName, final Properties parameters) throws JobStartException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001200.operator.start"), jslName);
        this.securityCheck.canStartJob(jslName);
        try {
            final io.machinecode.chainlink.spi.element.Job theirs = configuration.getJobLoader().load(jslName);
            final JobImpl job = JobFactory.produce(theirs, parameters);

            return _startJob(job, jslName, parameters).getJobExecutionId();
        } catch (final JobStartException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    @Override
    public JobOperationImpl startJob(final JobWork job, final String jslName, final Properties parameters) throws JobStartException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001200.operator.start"), jslName);
        this.securityCheck.canStartJob(jslName);
        try {
            return _startJob(job, jslName, parameters);
        } catch (final JobStartException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    private JobOperationImpl _startJob(final JobWork job, final String jslName, final Properties parameters) throws Exception {
        JobFactory.validate(job);

        final ExecutionRepository repository = transport.getExecutionRepository(this.executionRepositoryId);
        final ExtendedJobInstance instance = repository.createJobInstance(job, jslName, new Date());
        final ExtendedJobExecution execution = repository.createJobExecution(instance, parameters, new Date());
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
        final Deferred<?> deferred = executor.execute(jobExecutionId, new JobExecutable(
                null,
                this.executionRepositoryId,
                job,
                context
        ));
        return new JobOperationImpl(
                jobExecutionId,
                deferred,
                repository
        );
    }

    @Override
    public JobOperationImpl getJobOperation(final long jobExecutionId) throws JobExecutionNotRunningException {
        this.securityCheck.canAccessJobExecution(jobExecutionId);
        try {
            final Deferred<?> deferred = transport.getJob(jobExecutionId);
            return new JobOperationImpl(
                    jobExecutionId,
                    deferred,
                    transport.getExecutionRepository(this.executionRepositoryId)
            );
        } catch (final JobExecutionNotRunningException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobRestartException(e);
        }
    }

    @Override
    public long restart(final long jobExecutionId, final Properties parameters) throws JobExecutionAlreadyCompleteException, NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobRestartException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001201.operator.restart"), jobExecutionId);
        this.securityCheck.canRestartJob(jobExecutionId);
        try {
            final ExecutionRepository repository = transport.getExecutionRepository(this.executionRepositoryId);
            final ExtendedJobInstance instance = repository.getJobInstanceForExecution(jobExecutionId);
            final io.machinecode.chainlink.spi.element.Job theirs = configuration.getJobLoader().load(instance.getJslName());
            final JobImpl job = JobFactory.produce(theirs, parameters);
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

    @Override
    public JobOperationImpl restartJob(final long jobExecutionId, final Properties parameters) throws JobExecutionAlreadyCompleteException, NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobRestartException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001201.operator.restart"), jobExecutionId);
        this.securityCheck.canRestartJob(jobExecutionId);
        try {
            final ExecutionRepository repository = transport.getExecutionRepository(this.executionRepositoryId);
            final ExtendedJobInstance instance = repository.getJobInstanceForExecution(jobExecutionId);
            final io.machinecode.chainlink.spi.element.Job theirs = configuration.getJobLoader().load(instance.getJslName());
            final JobImpl job = JobFactory.produce(theirs, parameters);
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

    private JobOperationImpl _restart(final JobWork job, final long jobExecutionId, final JobInstance instance, final Properties parameters) throws Exception {
        JobFactory.validate(job);
        final ExecutionRepository repository = transport.getExecutionRepository(this.executionRepositoryId);
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
        final Deferred<?> deferred = executor.execute(restartExecutionId, new JobExecutable(
                null,
                this.executionRepositoryId,
                job,
                context
        ));
        return new JobOperationImpl(
                restartExecutionId,
                deferred,
                repository
        );
    }

    @Override
    public void stop(final long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException, JobSecurityException {
        this.securityCheck.canStopJob(jobExecutionId);
        stopJob(jobExecutionId);
    }

    @Override
    public Deferred<?> stopJob(final long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001202.operator.stop"), jobExecutionId);
        this.securityCheck.canRestartJob(jobExecutionId);
        try {
            final ExecutionRepository repository = transport.getExecutionRepository(this.executionRepositoryId);
            repository.getJobExecution(jobExecutionId); //This will throw a NoSuchJobExecutionException if required
            final Deferred<?> deferred = transport.getJob(jobExecutionId);
            if (deferred == null) {
                throw new JobExecutionNotRunningException(Messages.format("CHAINLINK-001002.operator.not.running", jobExecutionId));
            }
            executor.cancel(deferred);
            return deferred;
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobExecutionNotRunningException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    @Override
    public void abandon(final long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionIsRunningException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001203.operator.abandon"), jobExecutionId);
        this.securityCheck.canAbandonJob(jobExecutionId);
        try {
            try {
                transport.getJob(jobExecutionId);
                throw new JobExecutionIsRunningException(Messages.format("CHAINLINK-001001.operator.running", jobExecutionId));
            } catch (final JobExecutionNotRunningException e) {
                Repository.abandonedJob(transport.getExecutionRepository(this.executionRepositoryId), jobExecutionId);
            }
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobExecutionIsRunningException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    @Override
    public JobInstance getJobInstance(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        this.securityCheck.canAccessJobExecution(jobExecutionId);
        try {
        final JobInstance jobInstance = transport.getExecutionRepository(this.executionRepositoryId).getJobInstanceForExecution(jobExecutionId);
            this.securityCheck.canAccessJobInstance(jobInstance.getInstanceId());
            return jobInstance;
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    @Override
    public List<JobExecution> getJobExecutions(final JobInstance instance) throws NoSuchJobInstanceException, JobSecurityException {
        this.securityCheck.canAccessJobInstance(instance.getInstanceId());
        try {
            final ExecutionRepository repository = transport.getExecutionRepository(this.executionRepositoryId);
            final List<? extends JobExecution> jobExecutions =  repository.getJobExecutions(instance);
            final List<JobExecution> delegates = new ArrayList<JobExecution>(jobExecutions.size());
            for (final JobExecution jobExecution : jobExecutions) {
                if (!this.securityCheck.filterJobExecution(jobExecution.getExecutionId())) {
                    delegates.add(new DelegateJobExecution(jobExecution, repository));
                }
            }
            return delegates;
        } catch (final NoSuchJobInstanceException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    @Override
    public JobExecution getJobExecution(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        this.securityCheck.canAccessJobExecution(jobExecutionId);
        try {
            final ExecutionRepository repository = transport.getExecutionRepository(this.executionRepositoryId);
            return new DelegateJobExecution(repository.getJobExecution(jobExecutionId), repository);
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    @Override
    public List<StepExecution> getStepExecutions(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        this.securityCheck.canAccessJobExecution(jobExecutionId);
        try {
            final ExecutionRepository repository = transport.getExecutionRepository(this.executionRepositoryId);
            final List<? extends StepExecution> stepExecutions =  repository.getStepExecutionsForJobExecution(jobExecutionId);
            final List<StepExecution> delegates = new ArrayList<StepExecution>(stepExecutions.size());
            for (final StepExecution stepExecution : stepExecutions) {
                if (!this.securityCheck.filterStepExecution(stepExecution.getStepExecutionId())) {
                    delegates.add(new DelegateStepExecution(stepExecution, repository));
                }
            }
            return delegates;
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }
}
