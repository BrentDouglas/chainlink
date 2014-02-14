package io.machinecode.nock.core.work;

import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.context.MutableJobContext;
import io.machinecode.nock.spi.context.MutableStepContext;
import io.machinecode.nock.spi.context.ThreadId;
import io.machinecode.nock.spi.deferred.Deferred;
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

    public TaskExecutable(final Executable parent, final TaskWork work, final ExecutionContext context, final String stepId, final int partition, final int timeout) {
        super(parent, context, work, null);
        this.stepId = stepId;
        this.partition = partition;
        this.timeout = timeout;
    }

    @Override
    public Deferred<?> doExecute(final Executor executor, final ThreadId threadId, final Executable callback,
                                 final ExecutionContext childContext) throws Throwable {
        final MutableStepContext stepContext = context.getStepContext();
        final MutableJobContext jobContext = context.getJobContext();
        try {
            work.run(executor, this.context, timeout);
            return executor.callback(callback, this.context);
        } catch (final Throwable e) {
            log.errorf(e, Messages.format("NOCK-023004.work.task.run.exception", this.context));
            if (e instanceof Exception) {
                stepContext.setException((Exception) e);
            }
            stepContext.setBatchStatus(BatchStatus.FAILED);
            jobContext.setBatchStatus(BatchStatus.FAILED);
            return executor.callback(callback, this.context);
        }
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        return work.cancel(mayInterruptIfRunning)
                && super.cancel(mayInterruptIfRunning);
    }

    @Override
    protected Logger log() {
        return log;
    }
}
