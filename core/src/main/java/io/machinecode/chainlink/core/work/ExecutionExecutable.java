package io.machinecode.chainlink.core.work;

import io.machinecode.chainlink.core.jsl.impl.JobImpl;
import io.machinecode.chainlink.core.jsl.impl.execution.ExecutionImpl;
import io.machinecode.chainlink.core.registry.UUIDId;
import io.machinecode.chainlink.core.then.LinkAndRejectChain;
import io.machinecode.chainlink.core.then.LinkAndResolveChain;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.WorkerId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.registry.RepositoryId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.then.api.Promise;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class ExecutionExecutable extends ExecutableImpl<ExecutionImpl> {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(ExecutionExecutable.class);

    private final JobImpl job;

    public ExecutionExecutable(final JobImpl job, final ExecutableId parentId, final ExecutionImpl work, final ExecutionContext context, final RepositoryId repositoryId, final WorkerId workerId) {
        super(parentId, context, work, repositoryId, workerId);
        this.job = job;
    }

    @Override
    public void doExecute(final Configuration configuration, final Chain<?> chain, final WorkerId workerId, final ExecutableId parentId,
                                 final ExecutionContext previous) throws Throwable {
        final Registry registry = configuration.getRegistry();
        try {
            final Promise<Chain<?>,Throwable,?> next;
            //TODO Can link after cancel?
            if (chain.isCancelled()) {
                this.context.getJobContext().setBatchStatus(BatchStatus.STOPPING);
                next = configuration.getTransport().callback(parentId, this.context);
            } else {
                final ExecutableId callbackId = new UUIDId(configuration.getTransport());
                registry.registerExecutable(context.getJobExecutionId(), new ExecutionCallback(job, callbackId, parentId, this, workerId));
                next = work.before(job, configuration, this.repositoryId, workerId, callbackId, parentId, this.context);
            }
            next.onResolve(new LinkAndResolveChain(chain))
                    .onReject(chain)
                    .onCancel(chain);
        } catch (final Throwable e) {
            log.errorf(e, Messages.format("CHAINLINK-023000.work.execution.before.exception", this.context));
            if (this.context.getStepContext() != null) {
                this.context.getStepContext().setBatchStatus(BatchStatus.FAILED);
            }
            this.context.getJobContext().setBatchStatus(BatchStatus.FAILED);
            final Promise<Chain<?>,Throwable,?> next = configuration.getTransport().callback(parentId, this.context);
            next.onResolve(new LinkAndRejectChain(chain, e))
                    .onReject(chain)
                    .onCancel(chain);
        }
    }

    @Override
    protected Logger log() {
        return log;
    }
}
