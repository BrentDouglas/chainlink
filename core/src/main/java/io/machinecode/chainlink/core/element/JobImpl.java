package io.machinecode.chainlink.core.element;

import io.machinecode.chainlink.core.element.execution.ExecutionImpl;
import io.machinecode.chainlink.core.util.Repository;
import io.machinecode.chainlink.core.util.Statuses;
import io.machinecode.chainlink.core.validation.InvalidJobException;
import io.machinecode.chainlink.core.validation.JobTraversal;
import io.machinecode.chainlink.core.validation.JobValidator;
import io.machinecode.chainlink.core.work.ExecutionExecutable;
import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.element.Job;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.work.ExecutionWork;
import io.machinecode.chainlink.spi.work.JobWork;
import org.jboss.logging.Logger;

import javax.batch.api.listener.JobListener;
import javax.batch.runtime.BatchStatus;
import java.io.Serializable;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobImpl implements Job, JobWork, Serializable {

    private static final Logger log = Logger.getLogger(JobImpl.class);

    private final String id;
    private final String version;
    private final String restartable;
    private final PropertiesImpl properties;
    private final ListenersImpl listeners;
    private final List<ExecutionImpl> executions;
    private final JobTraversal traversal;

    private transient List<ListenerImpl> _listeners;

    public JobImpl(final String id, final String version, final String restartable, final PropertiesImpl properties,
                   final ListenersImpl listeners, final List<ExecutionImpl> executions) throws InvalidJobException {
        this.id = id;
        this.version = version;
        this.restartable = restartable;
        this.properties = properties;
        this.listeners = listeners;
        this.executions = executions;
        this.traversal = new JobTraversal(id, JobValidator.INSTANCE.visit(this));
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

    @Override
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

    private List<ListenerImpl> _listeners(final RuntimeConfiguration configuration, final ExecutionContext context) throws Exception {
        if (this._listeners == null) {
            this._listeners = this.listeners.getListenersImplementing(configuration, context, JobListener.class);
        }
        return _listeners;
    }

    @Override
    public Chain<?> before(final RuntimeConfiguration configuration, final ExecutionRepositoryId executionRepositoryId,
                              final WorkerId workerId, final ExecutableId callbackId, final ExecutionContext context) throws Exception {
        final ExecutionRepository repository = configuration.getExecutionRepository(executionRepositoryId);
        long jobExecutionId = context.getJobExecutionId();
        Repository.startedJob(repository, jobExecutionId);
        log.debugf(Messages.get("CHAINLINK-018000.job.create.job.context"), context);
        Exception exception = null;
        for (final ListenerImpl listener : this._listeners(configuration, context)) {
            try {
                log.debugf(Messages.get("CHAINLINK-018001.job.listener.before.job"), context);
                listener.beforeJob(configuration, context);
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
        if (Statuses.isStopping(batchStatus)) {
            log.debugf(Messages.get("CHAINLINK-018002.job.status.early.termination"), context, batchStatus);
            final Executable callback = configuration.getRegistry()
                    .getExecutableAndContext(jobExecutionId, callbackId)
                    .getExecutable();
            return configuration.getExecutor().callback(callback, context);
        }

        final Long restartJobExecutionId = context.getRestartJobExecutionId();
        if (restartJobExecutionId == null) {
            return _runNext(configuration, callbackId, context, executionRepositoryId, this.executions.get(0));
        }
        repository.linkJobExecutions(jobExecutionId, restartJobExecutionId);
        final String restartId = context.getRestartElementId();
        if (restartId == null) {
            return _runNext(configuration, callbackId, context, executionRepositoryId, this.executions.get(0));
        }
        log.debugf(Messages.get("CHAINLINK-018004.job.restart.transition"), context, restartId);
        return _runNext(configuration, callbackId, context, executionRepositoryId, traversal.next(restartId));
    }

    @Override
    public void after(final RuntimeConfiguration configuration, final ExecutionRepositoryId executionRepositoryId,
                      final WorkerId workerId, final ExecutableId callbackId, final ExecutionContext context) throws Exception {
        Exception exception = null;
        for (final ListenerImpl listener : this._listeners(configuration, context)) {
            try {
                log.debugf(Messages.get("CHAINLINK-018003.job.listener.after.job"), context);
                listener.afterJob(configuration, context);
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
    }

    @Override
    public ExecutionWork getNextExecution(final String next) {
        return traversal.next(next);
    }

    private static Chain<?> _runNext(final RuntimeConfiguration configuration, final ExecutableId callbackId,
                                        final ExecutionContext context, final ExecutionRepositoryId executionRepositoryId,
                                        final ExecutionWork next) throws Exception {
        return configuration.getExecutor().execute(new ExecutionExecutable(
                callbackId,
                next,
                context,
                executionRepositoryId,
                null
        ));
    }
}
