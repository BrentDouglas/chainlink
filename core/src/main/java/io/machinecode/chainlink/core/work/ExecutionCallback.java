package io.machinecode.chainlink.core.work;

import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.ThreadId;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.work.ExecutionWork;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class ExecutionCallback extends ExecutableImpl<ExecutionWork> {

    private static final Logger log = Logger.getLogger(ExecutionCallback.class);

    public ExecutionCallback(final ExecutableImpl<ExecutionWork> executable, final ThreadId threadId) {
        super(executable, threadId);
    }

    @Override
    public Deferred<?> doExecute(final Executor executor, final ThreadId threadId, final Executable callback,
                                 final ExecutionContext childContext) throws Throwable {
        try{
            if (isCancelled()) {
                this.context.getJobContext().setBatchStatus(BatchStatus.STOPPING);
            }
            return work.after(executor, threadId, callback, this.context, childContext);
        } catch (final Throwable e) {
            log.errorf(e, Messages.format("CHAINLINK-023001.work.execution.after.exception", this.context));
            if (this.context.getStepContext() != null) {
                this.context.getStepContext().setBatchStatus(BatchStatus.FAILED);
            }
            this.context.getJobContext().setBatchStatus(BatchStatus.FAILED);
            return executor.callback(callback, this.context);
        }
    }

    @Override
    public boolean willSpawnCallback() {
        return false;
    }

    @Override
    protected Logger log() {
        return log;
    }
}
