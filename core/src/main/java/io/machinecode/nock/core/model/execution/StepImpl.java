package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.Constants;
import io.machinecode.nock.core.impl.StepContextImpl;
import io.machinecode.nock.core.model.ListenersImpl;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyImpl;
import io.machinecode.nock.core.model.partition.PartitionImpl;
import io.machinecode.nock.core.model.transition.TransitionImpl;
import io.machinecode.nock.core.util.PropertiesConverter;
import io.machinecode.nock.core.work.PlanImpl;
import io.machinecode.nock.core.work.task.RunTask;
import io.machinecode.nock.spi.ExecutionRepository;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.execution.Step;
import io.machinecode.nock.spi.transport.Plan;
import io.machinecode.nock.spi.transport.TargetThread;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.util.Message;
import io.machinecode.nock.spi.work.ExecutionWork;
import io.machinecode.nock.spi.work.PartitionTarget;
import io.machinecode.nock.spi.work.StrategyWork;
import io.machinecode.nock.spi.work.TaskWork;
import org.jboss.logging.Logger;

import javax.batch.api.listener.StepListener;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.StepExecution;
import javax.batch.runtime.context.StepContext;
import java.util.Date;
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

    @Override
    public String element() {
        return ELEMENT;
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
                    log.debugf(Message.get("step.transaction.timeout.not.integer"), jobExecutionId, id, property.getValue());
                    break;
                }
            }
        return this._timeout;
    }

    // Lifecycle

    @Override
    public Plan before(final Transport transport, final Context context) throws Exception {
        final ExecutionRepository repository = transport.getRepository();
        final JobExecution jobExecution = repository.getJobExecution(context.getJobExecutionId());
        final StepExecution stepExecution = repository.createStepExecution(jobExecution, this);
        final long jobExecutionId = context.getJobExecutionId();
        if (stepExecution.getBatchStatus() != BatchStatus.STARTING) {
            throw new IllegalStateException(Message.format("step.not.starting", jobExecutionId, id, stepExecution.getBatchStatus()));
        }
        final StepContextImpl stepContext = new StepContextImpl(stepExecution, PropertiesConverter.convert(this.properties));
        log.debugf(Message.get("step.create.step.context"), jobExecutionId, id);
        context.setStepContext(stepContext);
        Exception exception = null;
        this._listeners = this.listeners.getListenersImplementing(transport, context, StepListener.class);
        for (final StepListener listener : this._listeners) {
            try {
                log.debugf(Message.get("step.listener.before.step"), jobExecutionId, id);
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
        return null;
    }

    @Override
    public Plan run(final Transport transport, final Context context) throws Exception {
        final long jobExecutionId = context.getJobExecutionId();
        int timeout = _timeout(jobExecutionId);
        if (this.partition != null && this.partition.getStrategy() != null) { //TODO This looks like a bug in the xsl
            final PartitionTarget target = this.partition.map(this.task, transport, context, timeout);
            return new PlanImpl(target.threads, target.executables, TargetThread.ANY, this.task.element());
        }
        return new PlanImpl(new RunTask(this.task, context, timeout), TargetThread.ANY, this.task.element());
    }

    @Override
    public Plan after(final Transport transport, final Context context) throws Exception {
        final long jobExecutionId = context.getJobExecutionId();
        int timeout = _timeout(jobExecutionId);
        final ExecutionWork execution;
        try {
            if (this.partition != null) {
                this.partition.analyse(this.task, transport, context, timeout);
            }
            Exception exception = null;
            if (this._listeners == null) {
                throw new IllegalStateException();
            }
            for (final StepListener listener : this._listeners) {
                try {
                    log.debugf(Message.get("step.listener.after.step"), jobExecutionId, id);
                    listener.afterStep();
                } catch (final Exception e) {
                    if (exception == null) {
                        exception = e;
                    } else {
                        exception.addSuppressed(e);
                    }
                }
            }
            final StepContext stepContext = context.getStepContext();
            final ExecutionRepository repository = transport.getRepository();
            log.debugf(Message.get("step.update.persistent.data"), jobExecutionId, id);
            repository.updateStepExecution(
                    stepContext.getStepExecutionId(),
                    stepContext.getPersistentUserData(),
                    new Date()
            );
            if (exception != null) {
                throw exception;
            }
            execution = this.transitionOrSetStatus(transport, context, this.transitions, this.next);
        } finally {
            log.debugf(Message.get("step.destroy.step.context"), jobExecutionId, id);
            context.setStepContext(null);
        }
        if (execution != null) {
            return execution.plan(transport, context);
        }
        return null;
    }
}
