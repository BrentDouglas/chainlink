package io.machinecode.nock.core.work;

import io.machinecode.nock.core.deferred.DeferredImpl;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.context.ThreadId;
import io.machinecode.nock.spi.deferred.Deferred;
import io.machinecode.nock.spi.execution.CallbackExecutable;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.util.Messages;
import io.machinecode.nock.spi.work.JobWork;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.context.JobContext;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class JobExecutable extends CallbackExecutableImpl<JobWork> {

    private static final Logger log = Logger.getLogger(JobExecutable.class);

    public JobExecutable(final JobWork work, final ExecutionContext context) {
        super(context, work);
    }

    @Override
    public Deferred<?,?> doExecute(final Executor executor, final ThreadId threadId, final CallbackExecutable parentExecutable,
                                   final ExecutionContext... _) throws Throwable {
        try {
            return work.before(executor, threadId, this, parentExecutable, context);
        } catch (final Throwable e) {
            log.errorf(e, Messages.format("work.job.before.exception", context.getJobExecutionId()));
            context.getJobContext().setBatchStatus(BatchStatus.FAILED);
            throw e;
        }
    }

    @Override
    protected Deferred<?, ?> doCallback(final Executor executor, final ThreadId threadId, final CallbackExecutable parentExecutable,
                                        final ExecutionContext childContext) throws Throwable {
        final JobContext jobContext = context.getJobContext();
        try {
            work.after(executor, threadId, this, parentExecutable, context, childContext);
            return new DeferredImpl<Void,Throwable>();
        } catch (final Throwable e) {
            log.errorf(e, Messages.format("work.job.after.exception", context.getJobExecutionId()));
            context.getJobContext().setBatchStatus(BatchStatus.FAILED);
            throw e;
        } finally {
            if (BatchStatus.FAILED.equals(childContext.getJobContext().getBatchStatus())) {
                RepositoryStatus.failedJob(
                        executor.getRepository(),
                        context.getJobExecutionId(),
                        jobContext.getExitStatus()
                );
            } else if (BatchStatus.STOPPING.equals(childContext.getJobContext().getBatchStatus())) {
                RepositoryStatus.finishJob(
                        executor.getRepository(),
                        context.getJobExecutionId(),
                        BatchStatus.STOPPED,
                        jobContext.getExitStatus()
                );
            } else {
                RepositoryStatus.completedJob(
                        executor.getRepository(),
                        context.getJobExecutionId(),
                        jobContext.getExitStatus()
                );
            }
            executor.removeJob(context.getJobExecutionId());
        }
    }
}
