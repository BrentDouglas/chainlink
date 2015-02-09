package io.machinecode.chainlink.core.work;

import io.machinecode.chainlink.core.jsl.impl.JobImpl;
import io.machinecode.chainlink.core.then.ResolvedChain;
import io.machinecode.chainlink.core.util.Repo;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.MutableJobContext;
import io.machinecode.chainlink.spi.execution.WorkerId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.then.api.Promise;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class JobCallback extends ExecutableImpl<JobImpl> {

    private static final Logger log = Logger.getLogger(JobCallback.class);

    final ExecutableId id;
    final Chain<?> chain;

    public JobCallback(final ExecutableId id, final ExecutableImpl<JobImpl> executable, final WorkerId workerId, final Chain<?> chain) {
        super(null, executable, workerId);
        this.id = id;
        this.chain = chain;
    }

    @Override
    public ExecutableId getId() {
        return this.id;
    }

    @Override
    protected void doExecute(final Configuration configuration, final Chain<?> chain, final WorkerId workerId,
                             final ExecutableId parentId, final ExecutionContext previous) throws Throwable {
        final MutableJobContext jobContext = context.getJobContext();
        Throwable throwable = null;
        try {
            work.after(configuration, this.repositoryId, workerId, parentId, this.context);
        } catch (final Throwable e) {
            log.errorf(e, Messages.format("CHAINLINK-023003.work.job.after.exception", context));
            jobContext.setBatchStatus(BatchStatus.FAILED);
            throwable = e;
        } finally {
            final BatchStatus batchStatus = jobContext.getBatchStatus();
            if (BatchStatus.FAILED.equals(batchStatus)) {
                Repo.failedJob(
                        Repo.getRepository(configuration, this.repositoryId),
                        context.getJobExecutionId(),
                        jobContext.getExitStatus()
                );
            } else if (BatchStatus.STOPPING.equals(batchStatus)) {
                Repo.finishJob(
                        Repo.getRepository(configuration, this.repositoryId),
                        context.getJobExecutionId(),
                        BatchStatus.STOPPED,
                        jobContext.getExitStatus(),
                        context.getRestartElementId()
                );
            } else {
                Repo.completedJob(
                        Repo.getRepository(configuration, this.repositoryId),
                        context.getJobExecutionId(),
                        jobContext.getExitStatus()
                );
            }
            final Promise<?,?,?> promise = configuration.getRegistry().unregisterJob(context.getJobExecutionId());
            if (throwable == null) {
                chain.linkAndResolve(null, new ResolvedChain<Void>(null));
            } else {
                chain.linkAndReject(throwable, new ResolvedChain<Void>(null));
            }
            final Transport transport = configuration.getTransport();
            promise.get(transport.getTimeout(), transport.getTimeUnit());
        }
    }

    @Override
    protected Logger log() {
        return log;
    }
}
