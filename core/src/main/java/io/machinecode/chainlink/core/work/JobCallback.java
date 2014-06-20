package io.machinecode.chainlink.core.work;

import io.machinecode.chainlink.core.util.Repository;
import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.MutableJobContext;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.work.JobWork;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.core.then.ResolvedChain;
import io.machinecode.then.api.OnResolve;
import io.machinecode.then.api.Promise;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class JobCallback extends ExecutableImpl<JobWork> {

    private static final Logger log = Logger.getLogger(JobCallback.class);

    public JobCallback(final ExecutableImpl<JobWork> executable, final WorkerId workerId) {
        super(null, executable, workerId);
    }

    @Override
    protected void doExecute(final RuntimeConfiguration configuration, final Chain<?> chain, final WorkerId workerId,
                                    final ExecutableId parentId, final ExecutionContext childContext) throws Throwable {
        final MutableJobContext jobContext = context.getJobContext();
        Throwable throwable = null;
        try {
            work.after(configuration, this.executionRepositoryId, workerId, parentId, this.context);
        } catch (final Throwable e) {
            log.errorf(e, Messages.format("CHAINLINK-023003.work.job.after.exception", context));
            jobContext.setBatchStatus(BatchStatus.FAILED);
            throwable = e;
        } finally {
            final BatchStatus batchStatus = jobContext.getBatchStatus();
            if (BatchStatus.FAILED.equals(batchStatus)) {
                Repository.failedJob(
                        configuration.getExecutionRepository(this.executionRepositoryId),
                        context.getJobExecutionId(),
                        jobContext.getExitStatus()
                );
            } else if (BatchStatus.STOPPING.equals(batchStatus)) {
                Repository.finishJob(
                        configuration.getExecutionRepository(this.executionRepositoryId),
                        context.getJobExecutionId(),
                        BatchStatus.STOPPED,
                        jobContext.getExitStatus(),
                        context.getRestartElementId()
                );
            } else {
                Repository.completedJob(
                        configuration.getExecutionRepository(this.executionRepositoryId),
                        context.getJobExecutionId(),
                        jobContext.getExitStatus()
                );
            }
            configuration.getRegistry().unregisterJob(context.getJobExecutionId());
            chain.link(new ResolvedChain<Void>(null));
            if (throwable == null) {
                chain.resolve(null);
            } else {
                chain.reject(throwable);
            }
            // TODO There is a race here that needs resolving
            // Possibly need to change the way JobAlreadyRunning is found or similar
        }
    }

    @Override
    protected Logger log() {
        return log;
    }
}
