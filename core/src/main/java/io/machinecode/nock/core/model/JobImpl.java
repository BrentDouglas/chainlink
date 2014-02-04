package io.machinecode.nock.core.model;

import io.machinecode.nock.core.impl.JobContextImpl;
import io.machinecode.nock.core.model.execution.ExecutionImpl;
import io.machinecode.nock.core.util.PropertiesConverter;
import io.machinecode.nock.core.work.ExecutionExecutable;
import io.machinecode.nock.core.work.RepositoryStatus;
import io.machinecode.nock.core.work.JobExecutable;
import io.machinecode.nock.jsl.validation.InvalidJobException;
import io.machinecode.nock.jsl.validation.JobValidator;
import io.machinecode.nock.jsl.visitor.JobTraversal;
import io.machinecode.nock.spi.ExecutionRepository;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.context.ThreadId;
import io.machinecode.nock.spi.deferred.Deferred;
import io.machinecode.nock.spi.element.Element;
import io.machinecode.nock.spi.element.Job;
import io.machinecode.nock.spi.element.execution.Step;
import io.machinecode.nock.spi.execution.CallbackExecutable;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.util.Messages;
import io.machinecode.nock.spi.util.Pair;
import io.machinecode.nock.spi.work.ExecutionWork;
import io.machinecode.nock.spi.work.JobWork;
import org.jboss.logging.Logger;

import javax.batch.api.listener.JobListener;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.StepExecution;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobImpl implements Job, JobWork {

    private static final Logger log = Logger.getLogger(JobImpl.class);

    private final String id;
    private final String version;
    private final String restartable;
    private final PropertiesImpl properties;
    private final ListenersImpl listeners;
    private final List<ExecutionImpl> executions;
    private final JobTraversal traversal;

    private transient List<JobListener> _listeners;

    public JobImpl(final String id, final String version, final String restartable, final PropertiesImpl properties,
                   final ListenersImpl listeners, final List<ExecutionImpl> executions) throws InvalidJobException {
        this.id = id;
        this.version = version;
        this.restartable = restartable;
        this.properties = properties;
        this.listeners = listeners;
        this.executions = executions;
        this.traversal = new JobTraversal(JobValidator.INSTANCE.visit(this));
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public String getRestartable() {
        return this.restartable;
    }

    public boolean isRestartable() {
        return Boolean.parseBoolean(this.restartable);
    }

    @Override
    public PropertiesImpl getProperties() {
        return this.properties;
    }

    @Override
    public ListenersImpl getListeners() {
        return this.listeners;
    }

    @Override
    public List<ExecutionImpl> getExecutions() {
        return this.executions;
    }

    // Lifecycle

    @Override
    public Deferred<?> before(final Executor executor, final ThreadId threadId, final CallbackExecutable thisExecutable,
                                 final CallbackExecutable parentExecutable, final ExecutionContext context) throws Exception {
        final ExecutionRepository repository = executor.getRepository();
        final JobContextImpl jobContext = new JobContextImpl(
                repository.getJobInstance(context.getJobInstanceId()),
                repository.getJobExecution(context.getJobExecutionId()),
                PropertiesConverter.convert(this.properties)
        );
        long jobExecutionId = jobContext.getExecutionId();
        jobContext.setBatchStatus(BatchStatus.STARTED);
        RepositoryStatus.startedJob(repository, jobExecutionId);

        log.debugf(Messages.get("job.create.job.context"), jobExecutionId, id);
        context.setJobContext(jobContext);
        this._listeners = this.listeners.getListenersImplementing(executor, context, JobListener.class);
        Exception exception = null;
        for (final JobListener listener : this._listeners) {
            try {
                log.debugf(Messages.get("job.listener.before.job"), jobExecutionId, id);
                listener.beforeJob();
            } catch (final Exception e) {
                if (exception == null) {
                    exception = e;
                } else {
                    exception.addSuppressed(e);
                }
            }
        }
        if (exception != null) {
            throw exception;
        }

        final BatchStatus batchStatus = context.getJobContext().getBatchStatus();
        if (RepositoryStatus.isStopping(batchStatus) || RepositoryStatus.isComplete(batchStatus)) {
            log.debugf(Messages.get("job.status.early.termination"), jobExecutionId, id, batchStatus);
            return null;
        }
        final String restartId = context.getJobExecution().getRestartId();
        if (restartId != null) {
            log.debugf(Messages.get("job.restart.transition"), jobExecutionId, id, restartId);
            final JobWork job = context.getJob();
            ExecutionWork next = job.getNextExecution(restartId);
            do {
                final StepExecution stepExecution;
                try {
                    stepExecution = repository.getStepExecution(jobExecutionId, id);
                } catch (final NoSuchJobExecutionException e) {
                    return _runNext(executor, context, next);
                }
                final BatchStatus bs = stepExecution.getBatchStatus();
                if (BatchStatus.FAILED == bs || BatchStatus.STOPPED == bs) {
                    return _runNext(executor, context, next);
                }
                if (BatchStatus.COMPLETED == bs && next instanceof Step) {
                    if (Boolean.parseBoolean(((Step) next).getAllowStartIfComplete())) {
                        return _runNext(executor, context, next);
                    }
                }
            } while ((next = job.getNextExecution(restartId)) != null);
            return null;
        }
        return _runNext(executor, context, this.executions.get(0));
    }

    @Override
    public void after(final Executor executor, final ThreadId threadId, final CallbackExecutable thisExecutable,
                      final CallbackExecutable parentExecutable, final ExecutionContext context, final ExecutionContext childContext) throws Exception {
        final long jobExecutionId = context.getJobExecutionId();
        try {
            if (this._listeners == null) {
                throw new IllegalStateException();
            }
            Exception exception = null;
            for (final JobListener listener : this._listeners) {
                try {
                    log.debugf(Messages.get("job.listener.after.job"), jobExecutionId, id);
                    listener.afterJob();
                } catch (final Exception e) {
                    if (exception == null) {
                        exception = e;
                    } else {
                        exception.addSuppressed(e);
                    }
                }
            }
            if (exception != null) {
                throw exception;
            }
        } finally {
            log.debugf(Messages.get("job.destroy.job.context"), jobExecutionId, id);
            context.setJobContext(null);
        }
    }

    @Override
    public ExecutionWork getNextExecution(final String next) {
        return traversal.next(next);
    }

    @Override
    public List<? extends Pair<String, String>> getProperties(final Element element) {
        return traversal.properties(element);
    }

    private static Deferred<?> _runNext(final Executor executor, final ExecutionContext context, final ExecutionWork next) {
        return executor.execute(new ExecutionExecutable(next, context));
    }
}
