package io.machinecode.chainlink.core.work;

import io.machinecode.chainlink.core.deferred.DeferredImpl;
import io.machinecode.chainlink.core.util.Repository;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.ThreadId;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.work.JobWork;
import org.jboss.logging.Logger;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class JobExecutable extends ExecutableImpl<JobWork> {

    private static final Logger log = Logger.getLogger(JobExecutable.class);

    public JobExecutable(final Executable parent, final JobWork work, final ExecutionContext context) {
        super(parent, context, work, null);
    }

    @Override
    public Deferred<?> doExecute(final Executor executor, final ThreadId threadId, final Executable callback,
                                 final ExecutionContext _) throws Throwable {
        try {
            return work.before(executor, threadId, new JobCallback(this, threadId), this.context);
        } catch (final Throwable e) {
            log.errorf(e, Messages.format("CHAINLINK-023002.work.job.before.exception", this.context));
            Repository.failedJob(
                    executor.getRepository(),
                    this.context.getJobExecutionId(),
                    this.context.getJobContext().getExitStatus()
            );
            executor.removeJob(this.context.getJobExecutionId());
            final Deferred<?> deferred = new DeferredImpl<Void>();
            deferred.reject(e);
            return deferred;
        }
    }

    @Override
    protected Logger log() {
        return log;
    }
}
