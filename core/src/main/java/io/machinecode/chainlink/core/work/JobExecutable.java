package io.machinecode.chainlink.core.work;

import io.machinecode.chainlink.core.deferred.ResolvedDeferred;
import io.machinecode.chainlink.core.execution.UUIDExecutableId;
import io.machinecode.chainlink.core.util.Repository;
import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.transport.ExecutableId;
import io.machinecode.chainlink.spi.transport.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.transport.WorkerId;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.work.JobWork;
import org.jboss.logging.Logger;

import java.io.Serializable;
import java.util.UUID;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class JobExecutable extends ExecutableImpl<JobWork> implements Serializable {

    private static final Logger log = Logger.getLogger(JobExecutable.class);

    public JobExecutable(final ExecutableId parentId, final ExecutionRepositoryId executionRepositoryId,
                         final JobWork work, final ExecutionContext context) {
        super(parentId, context, work, executionRepositoryId, null);
    }

    @Override
    public void doExecute(final RuntimeConfiguration configuration, final Deferred<?> deferred, final WorkerId workerId,
                                 final ExecutableId parentId, final ExecutionContext _) throws Throwable {
        try {
            final ExecutableId callbackId = new UUIDExecutableId(UUID.randomUUID());
            configuration.getTransport().registerExecutable(context.getJobExecutionId(), callbackId, new JobCallback(this, workerId));
            final Deferred<?> next = work.before(configuration, this.executionRepositoryId, workerId, callbackId, this.context);
            deferred.link(next != null ? next : new ResolvedDeferred<Void>(null));
            deferred.resolve(null);
        } catch (final Throwable e) {
            log.errorf(e, Messages.format("CHAINLINK-023002.work.job.before.exception", this.context));
            Repository.failedJob(
                    configuration.getExecutionRepository(executionRepositoryId),
                    this.context.getJobExecutionId(),
                    this.context.getJobContext().getExitStatus()
            );
            configuration.getTransport().unregisterJob(this.context.getJobExecutionId());
            deferred.link(new ResolvedDeferred<Void>(null));
            deferred.reject(e);
        }
    }

    @Override
    protected Logger log() {
        return log;
    }
}
