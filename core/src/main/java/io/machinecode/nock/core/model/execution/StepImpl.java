package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.impl.StepContextImpl;
import io.machinecode.nock.core.model.ListenersImpl;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.partition.PartitionImpl;
import io.machinecode.nock.core.model.transition.TransitionImpl;
import io.machinecode.nock.core.util.PropertiesConverter;
import io.machinecode.nock.core.work.CompletedFuture;
import io.machinecode.nock.core.work.ExecutableImpl;
import io.machinecode.nock.core.work.execution.AfterExecution;
import io.machinecode.nock.core.work.task.RunTask;
import io.machinecode.nock.spi.Repository;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.execution.Step;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.transport.Executable;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.ExecutionWork;
import io.machinecode.nock.spi.work.StrategyWork;
import io.machinecode.nock.spi.work.TaskWork;
import io.machinecode.nock.spi.work.Worker;

import javax.batch.api.listener.StepListener;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.StepExecution;
import javax.batch.runtime.context.StepContext;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StepImpl<T extends TaskWork, U extends StrategyWork> extends ExecutionImpl implements Step<T, U> {

    private final String next;
    private final String startLimit;
    private final String allowStartIfComplete;
    private final PropertiesImpl properties;
    private final ListenersImpl listeners;
    private final List<TransitionImpl> transitions;
    private final T task;
    private final PartitionImpl<U> partition;

    private transient List<StepListener> _listeners;

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

    // Lifecycle

    @Override
    public Future<Void> before(final Worker worker, final Transport transport, final Context context) throws Exception {
        final Repository repository = transport.getRepository();
        final JobExecution jobExecution = repository.getJobExecution(context.getJobExecutionId());
        final StepExecution stepExecution = repository.createStepExecution(jobExecution, this);
        final StepContextImpl stepContext = new StepContextImpl(stepExecution, PropertiesConverter.convert(this.properties));
        context.setStepContext(stepContext);
        Exception exception = null;
        final InjectionContext injectionContext = transport.createInjectionContext(context);
        this._listeners = this.listeners.getListenersImplementing(injectionContext, StepListener.class);
        for (final StepListener listener : this._listeners) {
            try {
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
        return CompletedFuture.INSTANCE;
    }

    @Override
    public Future<Void> run(final Worker worker, final Transport transport, final Context context) throws Exception {
        final AfterExecution after = new AfterExecution(worker, this, context);
        if (this.partition != null
                && this.partition.getStrategy() != null) { //TODO This looks like a bug in the xsl
            final Executable[] tasks = this.partition.map(this.task, worker, transport, context); //TODO This can throw
            return transport.executeOnAnyThreadThenOnThisThread(tasks, after);
        }
        final ExecutableImpl executable = new RunTask(worker, this.task, context);
        executable.then(transport, after);
        return transport.executeOnThisThread(executable);
    }

    @Override
    public Future<Void> after(final Worker worker, final Transport transport, final Context context) throws Exception {
        try {
            if (this.partition != null) {
                this.partition.analyse(this.task, worker, transport, context);
            }
            Exception exception = null;
            if (this._listeners == null) {
                throw new IllegalStateException();
            }
            for (final StepListener listener : this._listeners) {
                try {
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
            final Repository repository = transport.getRepository();
            repository.updateStepExecution(stepContext.getStepExecutionId(), stepContext.getPersistentUserData(), new Date());
            if (exception != null) {
                throw exception;
            }
        } finally {
            context.setStepContext(null);
        }
        final ExecutionWork execution = worker.transitionOrSetStatus(transport, context, this.transitions, this.next);
        if (execution != null) {
            return worker.runExecution(execution, transport, context);
        }
        return CompletedFuture.INSTANCE;
    }


}
