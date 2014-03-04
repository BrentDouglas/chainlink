package io.machinecode.chainlink.core.work;

import io.machinecode.chainlink.core.deferred.DeferredImpl;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.MutableJobContext;
import io.machinecode.chainlink.spi.context.ThreadId;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.work.JobWork;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class JobCallback extends ExecutableImpl<JobWork> {

    private static final Logger log = Logger.getLogger(JobCallback.class);

    public JobCallback(final ExecutableImpl<JobWork> executable, final ThreadId threadId) {
        super(executable, null);
    }

    @Override
    protected Deferred<?> doExecute(final Executor executor, final ThreadId threadId, final Executable callback,
                                    final ExecutionContext childContext) throws Throwable {
        final MutableJobContext jobContext = context.getJobContext();
        jobContext.setFrom(childContext.getJobContext());
        final Deferred<?> deferred = new DeferredImpl<Void>();
        try {
            work.after(executor, threadId, callback, childContext);
            deferred.resolve(null);
            return deferred;
        } catch (final Throwable e) {
            log.errorf(e, Messages.format("CHAINLINK-023003.work.job.after.exception", context));
            jobContext.setBatchStatus(BatchStatus.FAILED);
            deferred.reject(e);
            return deferred;
        } finally {
            final BatchStatus batchStatus = jobContext.getBatchStatus();
            if (BatchStatus.FAILED.equals(batchStatus)) {
                Repository.failedJob(
                        executor.getRepository(),
                        context.getJobExecutionId(),
                        jobContext.getExitStatus()
                );
            } else if (BatchStatus.STOPPING.equals(batchStatus)) {
                Repository.finishJob(
                        executor.getRepository(),
                        context.getJobExecutionId(),
                        BatchStatus.STOPPED,
                        jobContext.getExitStatus(),
                        context.getRestartElementId()
                );
            } else {
                Repository.completedJob(
                        executor.getRepository(),
                        context.getJobExecutionId(),
                        jobContext.getExitStatus()
                );
            }
            executor.removeJob(context.getJobExecutionId());
        }
    }

    @Override
    protected Logger log() {
        return log;
    }
}
