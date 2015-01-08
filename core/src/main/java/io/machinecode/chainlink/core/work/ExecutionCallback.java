package io.machinecode.chainlink.core.work;

import io.machinecode.chainlink.core.then.ResolvedChain;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.work.ExecutionWork;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class ExecutionCallback extends ExecutableImpl<ExecutionWork> {

    private static final Logger log = Logger.getLogger(ExecutionCallback.class);

    final ExecutableId id;

    public ExecutionCallback(final ExecutableId id, final ExecutableId parentId, final ExecutableImpl<ExecutionWork> executable, final WorkerId workerId) {
        super(parentId, executable, workerId);
        this.id = id;
    }

    @Override
    public ExecutableId getId() {
        return this.id;
    }

    @Override
    public void doExecute(final Configuration configuration, final Chain<?> chain, final WorkerId workerId,
                          final ExecutableId parentId, final ExecutionContext childContext) throws Throwable {
        Chain<?> next;
        try {
            if (chain.isCancelled()) {
                context.getJobContext().setBatchStatus(BatchStatus.STOPPING);
            }
            next = work.after(configuration, this.executionRepositoryId, workerId, parentId, context, childContext);
            chain.linkAndResolve(null, next != null ? next : new ResolvedChain<Void>(null));
        } catch (final Throwable e) {
            log.errorf(e, Messages.format("CHAINLINK-023001.work.execution.after.exception", context));
            if (context.getStepContext() != null) {
                context.getStepContext().setBatchStatus(BatchStatus.FAILED);
            }
            context.getJobContext().setBatchStatus(BatchStatus.FAILED);
            next = configuration.getExecutor().callback(parentId, context);
            chain.linkAndReject(e, next != null ? next : new ResolvedChain<Void>(null));
        }
    }

    @Override
    protected Logger log() {
        return log;
    }
}
