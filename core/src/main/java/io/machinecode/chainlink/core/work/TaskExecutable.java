package io.machinecode.chainlink.core.work;

import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.MutableJobContext;
import io.machinecode.chainlink.spi.context.MutableStepContext;
import io.machinecode.chainlink.spi.context.ThreadId;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.work.TaskWork;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class TaskExecutable extends ExecutableImpl<TaskWork> {

    private static final Logger log = Logger.getLogger(TaskExecutable.class);

    final int timeout;

    public TaskExecutable(final Executable parent, final TaskWork work, final ExecutionContext context, final int timeout) {
        super(parent, context, work, null);
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
            log.errorf(e, Messages.format("CHAINLINK-023004.work.task.run.exception", this.context));
            if (e instanceof Exception) {
                stepContext.setException((Exception) e);
            }
            stepContext.setBatchStatus(BatchStatus.FAILED);
            jobContext.setBatchStatus(BatchStatus.FAILED);
            return executor.callback(callback, this.context);
        }
    }

    @Override
    public boolean willSpawnCallback() {
        return false;
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
