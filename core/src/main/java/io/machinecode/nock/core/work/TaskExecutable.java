package io.machinecode.nock.core.work;

import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.context.MutableStepContext;
import io.machinecode.nock.spi.context.ThreadId;
import io.machinecode.nock.spi.deferred.Deferred;
import io.machinecode.nock.spi.execution.CallbackExecutable;
import io.machinecode.nock.spi.execution.Executable;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.util.Messages;
import io.machinecode.nock.spi.work.TaskWork;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class TaskExecutable extends ExecutableImpl<TaskWork> {

    private static final Logger log = Logger.getLogger(TaskExecutable.class);

    final String stepId;
    final int partition;
    final int timeout;

    public TaskExecutable(final CallbackExecutable parent, final TaskWork work, final ExecutionContext context, final String stepId, final int partition, final int timeout) {
        super(parent, context, work);
        this.stepId = stepId;
        this.partition = partition;
        this.timeout = timeout;
    }

    @Override
    public Deferred<?> doExecute(final Executor executor, final ThreadId threadId, final CallbackExecutable parentExecutable,
                                   final ExecutionContext... contexts) throws Throwable {
        final MutableStepContext stepContext = context.getStepContext();
        try {
            work.run(executor, context, timeout); //TODO
            return executor.callback(parentExecutable, context);
        } catch (final Throwable e) {
            if (e instanceof Exception) {
                stepContext.setException((Exception) e);
            }
            log.errorf(e, Messages.format("work.task.run.exception", context.getJobExecutionId()));
            context.getStepContext().setBatchStatus(BatchStatus.FAILED);
            context.getJobContext().setBatchStatus(BatchStatus.FAILED);
            throw e;
        }
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        return work.cancel(mayInterruptIfRunning)
                && super.cancel(mayInterruptIfRunning);
    }
}
