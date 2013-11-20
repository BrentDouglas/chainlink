package io.machinecode.nock.core.work;

import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.context.ThreadId;
import io.machinecode.nock.spi.deferred.Deferred;
import io.machinecode.nock.spi.execution.CallbackExecutable;
import io.machinecode.nock.spi.execution.Executable;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.execution.Item;
import io.machinecode.nock.spi.util.Messages;
import io.machinecode.nock.spi.work.ExecutionWork;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.context.JobContext;
import java.util.ArrayList;
import java.util.Collections;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class ExecutionExecutable extends CallbackExecutableImpl<ExecutionWork> {

    private static final Logger log = Logger.getLogger(ExecutionExecutable.class);

    public ExecutionExecutable(final ExecutionWork work, final ExecutionContext context) {
        super(context, work);
    }

    @Override
    public Deferred<?,?> doExecute(final Executor executor, final ThreadId threadId, final CallbackExecutable parentExecutable,
                                   final ExecutionContext... contexts) throws Throwable {
        try {
            return work.before(executor, threadId, this, parentExecutable, context, contexts);
        } catch (final Throwable e) {
            log.errorf(e, Messages.format("work.execution.before.exception", context.getJobExecutionId()));
            if (context.getStepContext() != null) {
                context.getStepContext().setBatchStatus(BatchStatus.FAILED);
            }
            context.getJobContext().setBatchStatus(BatchStatus.FAILED);
            throw e;
        }
    }

    @Override
    public Deferred<?,?> doCallback(final Executor executor, final ThreadId threadId, final CallbackExecutable parentExecutable,
                                    final ExecutionContext childContext) throws Throwable {
        try {
            final JobContext jobContext = childContext.getJobContext();
            if (BatchStatus.FAILED.equals(jobContext.getBatchStatus())) {
                context.getJobContext().setBatchStatus(BatchStatus.FAILED);
            }
            if (BatchStatus.STOPPING.equals(jobContext.getBatchStatus())) {
                context.getJobContext().setBatchStatus(BatchStatus.STOPPING);
            }
            return work.after(executor, threadId, this, parentExecutable, context, childContext);
        } catch (final Throwable e) {
            log.errorf(e, Messages.format("work.execution.after.exception", context.getJobExecutionId()));
            if (context.getStepContext() != null) {
                context.getStepContext().setBatchStatus(BatchStatus.FAILED);
            }
            context.getJobContext().setBatchStatus(BatchStatus.FAILED);
            throw e;
        }
    }
}
