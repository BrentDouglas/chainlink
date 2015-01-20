package io.machinecode.chainlink.core.work;

import io.machinecode.chainlink.core.then.ResolvedChain;
import io.machinecode.chainlink.core.util.Repository;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.work.JobWork;
import org.jboss.logging.Logger;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class JobExecutable extends ExecutableImpl<JobWork> {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(JobExecutable.class);

    public JobExecutable(final ExecutableId parentId, final ExecutionRepositoryId executionRepositoryId,
                         final JobWork work, final ExecutionContext context) {
        super(parentId, context, work, executionRepositoryId, null);
    }

    @Override
    public void doExecute(final Configuration configuration, final Chain<?> chain, final WorkerId workerId,
                                 final ExecutableId parentId, final ExecutionContext childContext) throws Throwable {
        final Registry registry = configuration.getRegistry();
        try {
            final ExecutableId callbackId = configuration.getTransport().generateExecutableId();
            registry.registerExecutable(context.getJobExecutionId(), new JobCallback(callbackId, this, workerId, chain));
            final Chain<?> next = work.before(configuration, this.executionRepositoryId, workerId, callbackId, this.context);
            chain.linkAndResolve(null, next != null ? next : new ResolvedChain<Void>(null));
        } catch (final Throwable e) {
            log.errorf(e, Messages.format("CHAINLINK-023002.work.job.before.exception", this.context));
            Repository.failedJob(
                    Repository.getExecutionRepository(configuration, executionRepositoryId),
                    this.context.getJobExecutionId(),
                    this.context.getJobContext().getExitStatus()
            );
            chain.linkAndReject(e, new ResolvedChain<Void>(null));
            configuration.getRegistry()
                    .unregisterJob(this.context.getJobExecutionId())
                    .get();
        }
    }

    @Override
    protected Logger log() {
        return log;
    }
}
