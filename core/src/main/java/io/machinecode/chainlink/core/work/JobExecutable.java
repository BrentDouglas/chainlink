package io.machinecode.chainlink.core.work;

import io.machinecode.chainlink.core.util.Repository;
import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.work.JobWork;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.core.then.ResolvedChain;
import org.jboss.logging.Logger;

import java.io.Serializable;

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
        public void doExecute(final RuntimeConfiguration configuration, final Chain<?> chain, final WorkerId workerId,
                                 final ExecutableId parentId, final ExecutionContext _context) throws Throwable {
        final Registry registry = configuration.getRegistry();
        try {
            final ExecutableId callbackId = registry.generateExecutableId();
            registry.registerExecutable(context.getJobExecutionId(), new JobCallback(callbackId, this, workerId, chain));
            final Chain<?> next = work.before(configuration, this.executionRepositoryId, workerId, callbackId, this.context);
            chain.link(next != null ? next : new ResolvedChain<Void>(null));
            chain.resolve(null);
        } catch (final Throwable e) {
            log.errorf(e, Messages.format("CHAINLINK-023002.work.job.before.exception", this.context));
            Repository.failedJob(
                    configuration.getExecutionRepository(executionRepositoryId),
                    this.context.getJobExecutionId(),
                    this.context.getJobContext().getExitStatus()
            );
            chain.link(new ResolvedChain<Void>(null));
            chain.reject(e);
            configuration.getRegistry().unregisterJob(this.context.getJobExecutionId());
        }
    }

    @Override
    protected Logger log() {
        return log;
    }
}
