package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.Constants;
import io.machinecode.nock.core.impl.ExecutionContextImpl;
import io.machinecode.nock.core.impl.StepContextImpl;
import io.machinecode.nock.core.model.ListenersImpl;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyImpl;
import io.machinecode.nock.core.model.partition.PartitionImpl;
import io.machinecode.nock.core.model.transition.TransitionImpl;
import io.machinecode.nock.core.util.PropertiesConverter;
import io.machinecode.nock.core.work.RepositoryStatus;
import io.machinecode.nock.core.work.ExecutionExecutable;
import io.machinecode.nock.core.work.TaskExecutable;
import io.machinecode.nock.spi.ExecutionRepository;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.context.ThreadId;
import io.machinecode.nock.spi.element.execution.Step;
import io.machinecode.nock.spi.execution.CallbackExecutable;
import io.machinecode.nock.spi.execution.Executable;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.execution.Item;
import io.machinecode.nock.spi.util.Messages;
import io.machinecode.nock.spi.deferred.Deferred;
import io.machinecode.nock.spi.deferred.Listener;
import io.machinecode.nock.spi.work.PartitionTarget;
import io.machinecode.nock.spi.work.StrategyWork;
import io.machinecode.nock.spi.work.TaskWork;
import org.jboss.logging.Logger;

import javax.batch.api.listener.StepListener;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.StepExecution;
import javax.batch.runtime.context.StepContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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

    private transient List<StepListener> _listeners;
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

    public boolean isAllowStartIfComplete() {
        return Boolean.parseBoolean(this.allowStartIfComplete);
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

    private int _timeout(final long jobExecutionId) {
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
                    log.debugf(Messages.get("step.transaction.timeout.not.integer"), jobExecutionId, id, property.getValue());
                    break;
                }
            }
        return this._timeout;
    }

    // Lifecycle

    private boolean isPartitioned() {
        return this.partition != null && this.partition.getStrategy() != null;
    }

    private transient int partitions;
    private transient List<ExecutionContext> contexts;

    @Override
    public Deferred<?,?> before(final Executor executor, final ThreadId threadId, final CallbackExecutable thisExecutable,
                                final CallbackExecutable parentExecutable, final ExecutionContext parentContext,
                                final ExecutionContext... previousContexts) throws Exception {
        if (RepositoryStatus.isStopping(parentContext) || RepositoryStatus.isComplete(parentContext)) {
            return null; //TODO
        }
        final ExecutionRepository repository = executor.getRepository();
        final JobExecution jobExecution = repository.getJobExecution(parentContext.getJobExecutionId());
        final StepExecution stepExecution = repository.createStepExecution(jobExecution, this);
        final long stepExecutionId = stepExecution.getStepExecutionId();
        final ExecutionContext context = new ExecutionContextImpl(
                parentContext,
                this.id,
                stepExecutionId
        );
        final long jobExecutionId = context.getJobExecutionId();
        if (stepExecution.getBatchStatus() != BatchStatus.STARTING) {
            throw new IllegalStateException(Messages.format("step.not.starting", jobExecutionId, id, stepExecution.getBatchStatus()));
        }
        final StepContextImpl stepContext = new StepContextImpl(stepExecution, PropertiesConverter.convert(this.properties));
        log.debugf(Messages.get("step.create.step.context"), jobExecutionId, id);
        context.setStepContext(stepContext);
        //TODO Find out where this is meant to go
        repository.startStepExecution(stepExecutionId, stepContext.getMetrics(), new Date());
        Exception exception = null;
        this._listeners = this.listeners.getListenersImplementing(executor, context, StepListener.class);
        for (final StepListener listener : this._listeners) {
            try {
                log.debugf(Messages.get("step.listener.before.step"), jobExecutionId, id);
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

        int timeout = _timeout(jobExecutionId);

        if (!isPartitioned()) { //TODO This looks like a bug in the xsl
            this.partitions = 1;
            this.contexts = new ArrayList<ExecutionContext>(1);
            return executor.execute(new TaskExecutable(this.task, context, this.id, -1, timeout));
        } else {
            final PartitionTarget target = this.partition.map(this.task, executor, context, timeout);
            this.partitions = target.executables.length;
            this.contexts = new ArrayList<ExecutionContext>(this.partitions);
            return executor.execute(
                    target.threads,
                    target.executables
            );
        }
    }

    @Override
    public Deferred<?,?> after(final Executor executor, final ThreadId threadId, final CallbackExecutable thisExecutable,
                               final CallbackExecutable parentExecutable, final ExecutionContext context,
                               final ExecutionContext childContext) throws Exception {
        Collections.addAll(this.contexts, childContext);
        if (this.contexts.size() <= this.partitions) {
            return null; //TODO
        }
        final long jobExecutionId = context.getJobExecutionId();
        int timeout = _timeout(jobExecutionId);
        final StepContext stepContext = context.getStepContext();
        try {
            try {
                final LinkedList<Item> items = new LinkedList<Item>();
                for (final ExecutionContext partitionContext : this.contexts) {
                    Collections.addAll(items, partitionContext.getItems());
                }
                if (this.isPartitioned()) {
                    this.partition.analyse(this.task, executor, context, timeout, items);
                }
                Exception exception = null;
                if (this._listeners == null) {
                    throw new IllegalStateException(); //TODO Messages
                }
                for (final StepListener listener : this._listeners) {
                    try {
                        log.debugf(Messages.get("step.listener.after.step"), jobExecutionId, id);
                        listener.afterStep();
                    } catch (final Exception e) {
                        if (exception == null) {
                            exception = e;
                        } else {
                            exception.addSuppressed(e);
                        }
                    }
                }
                final ExecutionRepository repository = executor.getRepository();
                log.debugf(Messages.get("step.update.persistent.data"), jobExecutionId, id);
                repository.updateStepExecution(
                        stepContext.getStepExecutionId(),
                        stepContext.getPersistentUserData(),
                        new Date()
                );
                if (exception != null) {
                    throw exception;
                }
            } catch (final Throwable e) {
                RepositoryStatus.finishStep(
                        executor.getRepository(),
                        context.getJobExecutionId(),
                        BatchStatus.FAILED,
                        stepContext.getExitStatus()
                );
                context.getStepContext().setBatchStatus(BatchStatus.FAILED);
                context.getJobContext().setBatchStatus(BatchStatus.FAILED);
                return null;
            }
            return this.transition(executor, threadId, context, parentExecutable, this.transitions, this.next, stepContext.getExitStatus());
        } finally {
            log.debugf(Messages.get("step.destroy.step.context"), jobExecutionId, id);
            context.setStepContext(null);
        }
    }
}
