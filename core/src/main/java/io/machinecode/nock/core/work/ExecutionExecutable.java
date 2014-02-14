package io.machinecode.nock.core.work;

import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.context.ThreadId;
import io.machinecode.nock.spi.deferred.Deferred;
import io.machinecode.nock.spi.execution.Executable;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.util.Messages;
import io.machinecode.nock.spi.work.ExecutionWork;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class ExecutionExecutable extends ExecutableImpl<ExecutionWork> {

    private static final Logger log = Logger.getLogger(ExecutionExecutable.class);

    public ExecutionExecutable(final Executable parent, final ExecutionWork work, final ExecutionContext context) {
        super(parent, context, work, null);
    }

    public ExecutionExecutable(final Executable parent, final ExecutionWork work, final ExecutionContext context, final ThreadId threadId) {
        super(parent, context, work, threadId);
    }

    @Override
    public Deferred<?> doExecute(final Executor executor, final ThreadId threadId, final Executable callback,
                                 final ExecutionContext childContext) throws Throwable {
        try {
            if (isCancelled()) {
                this.context.getJobContext().setBatchStatus(BatchStatus.STOPPING);
                return executor.callback(callback, this.context);
            }
            return work.before(executor, threadId, new ExecutionCallback(this, threadId), callback, this.context);
        } catch (final Throwable e) {
            log.errorf(e, Messages.format("NOCK-023000.work.execution.before.exception", this.context));
            if (this.context.getStepContext() != null) {
                this.context.getStepContext().setBatchStatus(BatchStatus.FAILED);
            }
            this.context.getJobContext().setBatchStatus(BatchStatus.FAILED);
            return executor.callback(callback, this.context);
        }
    }

    @Override
    protected Logger log() {
        return log;
    }
}
