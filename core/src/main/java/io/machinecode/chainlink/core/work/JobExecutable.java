package io.machinecode.chainlink.core.work;

import io.machinecode.chainlink.core.context.ExecutionContextImpl;
import io.machinecode.chainlink.core.jsl.impl.JobImpl;
import io.machinecode.chainlink.core.registry.UUIDId;
import io.machinecode.chainlink.core.then.LinkAndResolveChain;
import io.machinecode.chainlink.core.then.ResolvedChain;
import io.machinecode.chainlink.core.util.Repo;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.WorkerId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.registry.RepositoryId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.then.api.Promise;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class JobExecutable extends ExecutableImpl<JobImpl> {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(JobExecutable.class);

    public JobExecutable(final ExecutableId parentId, final RepositoryId repositoryId,
                         final JobImpl work, final ExecutionContextImpl context) {
        super(parentId, context, work, repositoryId, null);
    }

    @Override
    public void doExecute(final Configuration configuration, final Chain<?> chain, final WorkerId workerId,
                          final ExecutableId parentId, final ExecutionContext previous) throws Throwable {
        final Registry registry = configuration.getRegistry();
        final Transport transport = configuration.getTransport();
        try {
            final ExecutableId callbackId = new UUIDId(transport);
            registry.registerExecutable(context.getJobExecutionId(), new JobCallback(callbackId, this, workerId, chain));
            final Promise<Chain<?>,Throwable,?> next = work.before(configuration, this.repositoryId, callbackId, this.context);
            next.onResolve(new LinkAndResolveChain(chain))
                    .onReject(chain)
                    .onCancel(chain);
        } catch (final Throwable e) {
            log.errorf(e, Messages.format("CHAINLINK-023002.work.job.before.exception", this.context));
            Repo.finishJob(
                    Repo.getRepository(configuration, repositoryId),
                    this.context.getJobExecutionId(),
                    BatchStatus.FAILED,
                    this.context.getJobContext().getExitStatus(),
                    null
            );
            chain.linkAndReject(e, new ResolvedChain<Void>(null));
            configuration.getRegistry()
                    .unregisterJob(this.context.getJobExecutionId())
                    .get(transport.getTimeout(), transport.getTimeUnit());
        }
    }

    @Override
    protected Logger log() {
        return log;
    }
}
