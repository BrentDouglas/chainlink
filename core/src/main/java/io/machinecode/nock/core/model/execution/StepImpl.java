package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.Constants;
import io.machinecode.nock.core.impl.StepContextImpl;
import io.machinecode.nock.core.model.ListenersImpl;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyImpl;
import io.machinecode.nock.core.model.partition.PartitionImpl;
import io.machinecode.nock.core.model.transition.TransitionImpl;
import io.machinecode.nock.core.util.PropertiesConverter;
import io.machinecode.nock.core.work.Repository;
import io.machinecode.nock.core.work.TaskExecutable;
import io.machinecode.nock.spi.ExecutionRepository;
import io.machinecode.nock.spi.ExtendedStepExecution;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.context.MutableJobContext;
import io.machinecode.nock.spi.context.MutableStepContext;
import io.machinecode.nock.spi.context.ThreadId;
import io.machinecode.nock.spi.element.execution.Step;
import io.machinecode.nock.spi.execution.Executable;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.util.Messages;
import io.machinecode.nock.spi.deferred.Deferred;
import io.machinecode.nock.spi.work.PartitionTarget;
import io.machinecode.nock.spi.work.StrategyWork;
import io.machinecode.nock.spi.work.TaskWork;
import io.machinecode.nock.spi.work.TransitionWork;
import org.jboss.logging.Logger;

import javax.batch.api.listener.StepListener;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.StepExecution;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static javax.batch.api.partition.PartitionReducer.PartitionStatus;
import static javax.batch.api.partition.PartitionReducer.PartitionStatus.COMMIT;
import static javax.batch.api.partition.PartitionReducer.PartitionStatus.ROLLBACK;
import static javax.batch.runtime.BatchStatus.COMPLETED;
import static javax.batch.runtime.BatchStatus.FAILED;
import static javax.batch.runtime.BatchStatus.STARTED;
import static javax.batch.runtime.BatchStatus.STARTING;
import static javax.batch.runtime.BatchStatus.STOPPED;
import static javax.batch.runtime.BatchStatus.STOPPING;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StepImpl<T extends TaskWork, U extends StrategyWork> extends ExecutionImpl implements Step<T, U> {

    private static final Logger log = Logger.getLogger(StepImpl.class);

    private final String next;
    private final String startLimit;
    private final String allowStartIfComplete;
    private final PropertiesImpl properties;
    private final ListenersImpl listeners;
    private final List<TransitionImpl> transitions;
    private final T task;
    private final PartitionImpl<U> partition;

    private transient Integer _timeout;

    public StepImpl(
            final String id,
            final String next,
            final String startLimit,
            final String allowStartIfComplete,
            final PropertiesImpl properties,
            final ListenersImpl listeners,
            final List<TransitionImpl> transitions,
            final T task,
            final PartitionImpl<U> partition
    ) {
        super(id);
        this.next = next;
        this.startLimit = startLimit;
        this.allowStartIfComplete = allowStartIfComplete;
        this.listeners = listeners;
        this.properties = properties;
        this.transitions = transitions;
        this.task = task;
        this.partition = partition;
    }

    @Override
    public String getNext() {
        return this.next;
    }

    @Override
    public String getStartLimit() {
        return this.startLimit;
    }

    @Override
    public String getAllowStartIfComplete() {
        return this.allowStartIfComplete;
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
    public List<TransitionImpl> getTransitions() {
        return this.transitions;
    }

    @Override
    public T getTask() {
        return this.task;
    }

    @Override
    public PartitionImpl<U> getPartition() {
        return this.partition;
    }

    private int _timeout(final ExecutionContext context) {
        if (_timeout != null) {
            return this._timeout;
        }
        this._timeout = 180;
        for (final PropertyImpl property : this.properties.getProperties()) {
            try {
                if (Constants.JAVAX_TRANSACTION_GLOBAL_TIMEOUT.equals(property.getName())) {
                    this._timeout = Integer.parseInt(property.getValue());
                    return this._timeout;
                }
            } catch (final NumberFormatException e) {
                throw new IllegalStateException(Messages.format("NOCK-010002.step.transaction.timeout.not.integer", context, this.id, property.getValue()));
            }
        }
        return this._timeout;
    }

    // Lifecycle

    private boolean isPartitioned() {
        //TODO This looks like a bug in the xsl
        return this.partition != null && this.partition.getStrategy() != null;
    }

    private transient List<StepListener> _listeners;
    private transient int _partitions;
    private transient int _completed;
    private transient PartitionStatus _partitionStatus;
    private transient TransactionManager _transactionManager;
    private transient Transaction _transaction;

    private List<StepListener> _listeners(final Executor executor, final ExecutionContext context) throws Exception {
        if (this._listeners == null) {
            this._listeners = this.listeners.getListenersImplementing(executor, context, StepListener.class);
        }
        return _listeners;
    }

    @Override
    public Deferred<?> before(final Executor executor, final ThreadId threadId, final Executable thisCallback,
                              final Executable parentCallback, final ExecutionContext context) throws Exception {
        log.debugf(Messages.get("NOCK-010100.step.before"), context, this.id);
        final ExecutionRepository repository = executor.getRepository();

        final MutableJobContext jobContext = context.getJobContext();
        final JobExecution jobExecution = repository.getJobExecution(jobContext.getExecutionId());
        StepExecution stepExecution;
        Serializable persistentData = null;
        Long restartStepExecutionId = null;
        if (context.isRestarting()) {
            final long restartJobExecutionId = context.getRestartJobExecution().getExecutionId();
            try {
                final ExtendedStepExecution restartStepExecution = repository.getLatestStepExecution(restartJobExecutionId, this.id);
                restartStepExecutionId = restartStepExecution.getStepExecutionId();
                log.debugf(Messages.get("NOCK-010206.step.found.existing.execution"), context, this.id, restartStepExecutionId, restartJobExecutionId);
                persistentData = restartStepExecution.getPersistentUserData();
                final BatchStatus batchStatus = restartStepExecution.getBatchStatus();
                final String exitStatus = restartStepExecution.getExitStatus();
                switch (batchStatus) {
                    case COMPLETED:
                        //TODO Apparently this should default to false if it is empty
                        if (!Boolean.parseBoolean(this.getAllowStartIfComplete())) {
                            /*
                             * a. If the execution element is a COMPLETED step that specifies allow-restart-if-
                             *    complete=false, then follow the transition to the next execution element based on the
                             *    exit status for this step from the previous execution.
                             */
                            context.setLastStepExecutionId(restartStepExecutionId);
                            final TransitionWork transition = this.transition(context, this.transitions, batchStatus, exitStatus);
                            if (transition != null && transition.isTerminating()) {
                                return runCallback(executor, context, parentCallback);
                            } else {
                                return this.next(executor, threadId, context, parentCallback, this.next, transition);
                            }
                        }
                        /*
                         * b. If the execution element is a COMPLETED step that specifies allow-restart-if-
                         *    complete=true, then re-run the step and follow the transition to the next execution
                         *    element.
                         */
                        break;
                    case FAILED:
                    case STOPPED:
                        /*
                         *c. If the execution element is a STOPPED or FAILED step then restart the step and follow
                         *   the transition to the next execution element.
                         *   Note if the step is a partitioned step, only the partitions that did not complete
                         *   previously are restarted. This behavior may be overridden through the PartitionPlan, as
                         *   specified in section 10.9.4 PartitionPlan.
                         */
                        break;
                    default:
                        throw new IllegalStateException(); //TODO Message
                }
            } catch (final NoSuchJobExecutionException e) {
                log.debugf(Messages.get("NOCK-010207.step.no.existing.execution"), context, this.id, restartJobExecutionId);
            }
            final int startLimit = Integer.parseInt(this.getStartLimit());
            final int startCount = repository.getStepExecutionCount(restartJobExecutionId, this.id);
            if (startLimit != 0 && startCount >= startLimit) {
                log.infof(Messages.get("NOCK-010213.step.start.limit.exceeded"), context, this.id, startCount, startLimit);
                jobContext.setBatchStatus(FAILED);
                return executor.callback(parentCallback, context);
            } else {
                log.infof(Messages.get("NOCK-010214.step.start.limit"), context, this.id, startCount, startLimit);
            }
        }
        stepExecution = repository.createStepExecution(jobExecution, this, new Date());

        final long stepExecutionId = stepExecution.getStepExecutionId();
        final MutableStepContext stepContext = new StepContextImpl(stepExecutionId, this, PropertiesConverter.convert(this.properties));
        context.setStepContext(stepContext);
        log.debugf(Messages.get("NOCK-010203.step.create.step.context"), context);
        stepContext.setPersistentUserData(persistentData);
        if (stepExecution.getBatchStatus() != STARTING) {
            throw new IllegalStateException(Messages.format("NOCK-010001.step.not.starting", context, stepExecution.getBatchStatus()));
        }
        Exception exception = null;
        for (final StepListener listener : this._listeners(executor, context)) {
            try {
                log.debugf(Messages.get("NOCK-010200.step.listener.before.step"), context);
                listener.beforeStep();
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
        context.setLastStepExecutionId(stepExecutionId);
        int timeout = _timeout(context);

        repository.startStepExecution(stepExecutionId, stepContext.getMetrics(), new Date());
        stepContext.setBatchStatus(STARTED);

        if (!isPartitioned()) {
            this._partitions = 1;
            this._completed = 0;
            return executor.execute(
                    new TaskExecutable(thisCallback, this.task, context, this.id, -1, timeout)
            );
        } else {
            final PartitionTarget target = this.partition.map(this.task, executor, thisCallback, context, timeout, restartStepExecutionId);
            this._partitions = target.executables.length;
            this._completed = 0;
            return executor.execute(
                    target.threads,
                    target.executables
            );
        }
    }

    @Override
    public Deferred<?> after(final Executor executor, final ThreadId threadId, final Executable callback,
                             final ExecutionContext context, final ExecutionContext childContext) throws Exception {
        final long jobExecutionId = context.getJobExecutionId();
        final long stepExecutionId = context.getStepExecutionId();
        log.debugf(Messages.get("NOCK-010101.step.after"), childContext);
        final ExecutionRepository repository = executor.getRepository();
        final MutableStepContext stepContext = context.getStepContext();
        ++this._completed;
        try {
            if (this.isPartitioned()) {
                if (this._completed == 1) {
                    this._transactionManager = executor.getTransactionManager();
                    this._partitionStatus = COMMIT;
                    int timeout = _timeout(context);
                    log.debugf(Messages.get("NOCK-010208.step.set.transaction.timeout"), childContext, timeout);
                    this._transactionManager.setTransactionTimeout(timeout);
                    log.debugf(Messages.get("NOCK-010210.step.begin.transaction"), childContext);
                    this._transactionManager.begin();
                } else if (this._partitionStatus == ROLLBACK) {
                    return null;
                } else {
                    log.debugf(Messages.get("NOCK-010212.step.resume.transaction"), childContext, this._transaction);
                    this._transactionManager.resume(this._transaction);
                }
                try {
                    switch (childContext.getStepContext().getBatchStatus()) {
                        case FAILED:
                            stepContext.setBatchStatus(FAILED);
                        case STOPPING:
                            if (stepContext.getBatchStatus() != FAILED) {
                                stepContext.setBatchStatus(STOPPING);
                            }
                    }
                    final PartitionStatus partitionStatus = this.partition.analyse(executor, context, this._transactionManager, childContext.getItems());
                    this._partitionStatus = this._partitionStatus == ROLLBACK
                            ? ROLLBACK
                            : partitionStatus;
                } catch (final Exception e) {
                    log.infof(e, Messages.get("NOCK-010209.step.analyse.exception"), childContext);
                    this._partitionStatus = ROLLBACK;
                }
                if (this._completed < this._partitions) {
                    this._transaction = this._transactionManager.suspend();
                    log.debugf(Messages.get("NOCK-010211.step.suspend.transaction"), childContext, this._transaction);
                    return null;
                }
                this.partition.reduce(stepContext.getBatchStatus() == FAILED ? ROLLBACK : this._partitionStatus, executor, context, this._transactionManager);
            } else {
                context.getJobContext().setFrom(childContext.getJobContext());
                stepContext.setFrom(childContext.getStepContext());
            }
        } catch (final Throwable e) {
            log.debugf(e, Messages.get("NOCK-010205.step.after.caught.exception"), context);
            this._partitionStatus = ROLLBACK;
            context.getJobContext().setBatchStatus(FAILED);
        }
        try {
            Exception exception = null;
            for (final StepListener listener : this._listeners(executor, context)) {
                try {
                    log.debugf(Messages.get("NOCK-010201.step.listener.after.step"), context);
                    listener.afterStep();
                } catch (final Exception e) {
                    if (exception == null) {
                        exception = e;
                    } else {
                        exception.addSuppressed(e);
                    }
                }
            }
            try {
                log.debugf(Messages.get("NOCK-010202.step.update.persistent.data"), context);
                Repository.updateStep(
                        repository,
                        jobExecutionId,
                        stepExecutionId,
                        stepContext.getPersistentUserData(),
                        stepContext.getMetrics()
                );
            } catch (final Exception e) {
                if (exception == null) {
                    exception = e;
                } else {
                    exception.addSuppressed(e);
                }
            }
            if (exception != null) {
                stepContext.setException(exception);
                stepContext.setBatchStatus(FAILED);
                throw exception;
            }
            final BatchStatus batchStatus;
            switch (stepContext.getBatchStatus()) {
                case STARTED:
                    batchStatus = COMPLETED;
                    break;
                case STOPPING:
                    batchStatus = STOPPED;
                    break;
                case FAILED:
                    batchStatus = FAILED;
                    break;
                default:
                    throw new IllegalStateException(Messages.format("NOCK-010000.step.illegal.batch.status", context, stepContext.getBatchStatus()));
            }
            context.getJobContext().setBatchStatus(stepContext.getBatchStatus());
            final String exitStatus = stepContext.getExitStatus();
            final TransitionWork transition = this.transition(context, this.transitions, batchStatus, exitStatus);
            Repository.finishStep(
                    repository,
                    jobExecutionId,
                    stepExecutionId,
                    batchStatus,
                    exitStatus,
                    stepContext.getMetrics()
            );
            if (transition != null && transition.isTerminating()) {
                return runCallback(executor, context, callback);
            } else {
                return this.next(executor, threadId, context, callback, this.next, transition);
            }
        } catch (final Throwable e) {
            log.debugf(e, Messages.get("NOCK-010205.step.after.caught.exception"), context);
            context.getJobContext().setBatchStatus(FAILED);
            Repository.finishStep(
                    repository,
                    jobExecutionId,
                    stepExecutionId,
                    FAILED,
                    stepContext.getExitStatus(),
                    stepContext.getMetrics()
            );
            return runCallback(executor, context, callback);
        } finally {
            log.debugf(Messages.get("NOCK-010204.step.destroy.step.context"), context);
            context.setStepContext(null);
        }
    }

    @Override
    protected Logger log() {
        return log;
    }
}
