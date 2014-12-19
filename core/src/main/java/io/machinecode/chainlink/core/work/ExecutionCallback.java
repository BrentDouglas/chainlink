package io.machinecode.chainlink.core.work;

import io.machinecode.chainlink.core.then.ResolvedChain;
import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.work.ExecutionWork;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
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
    public void doExecute(final RuntimeConfiguration configuration, final Chain<?> chain, final WorkerId workerId,
                          final ExecutableId parentId, final ExecutionContext childContext) throws Throwable {
        // This needs to be looked up rather than using this.context in case this is after a partitioned execution
        // and we are using remoting
        final ExecutionContext context = configuration.getRegistry()
                .getExecutableAndContext(this.context.getJobExecutionId(), this.getId())
                .getContext();
        Chain<?> next;
        try {
            if (chain.isCancelled()) {
                context.getJobContext().setBatchStatus(BatchStatus.STOPPING);
            }
            next = work.after(configuration, this.executionRepositoryId, workerId, parentId, context, childContext);
            chain.link(next != null ? next : new ResolvedChain<Void>(null));
            chain.resolve(null);
        } catch (final Throwable e) {
            log.errorf(e, Messages.format("CHAINLINK-023001.work.execution.after.exception", context));
            if (context.getStepContext() != null) {
                context.getStepContext().setBatchStatus(BatchStatus.FAILED);
            }
            context.getJobContext().setBatchStatus(BatchStatus.FAILED);
            final Executable callback = configuration.getRegistry()
                    .getExecutableAndContext(context.getJobExecutionId(), parentId)
                    .getExecutable();
            next = configuration.getExecutor().callback(callback, context);
            chain.link(next != null ? next : new ResolvedChain<Void>(null));
            chain.reject(e);
        }
    }

    @Override
    protected Logger log() {
        return log;
    }
}
