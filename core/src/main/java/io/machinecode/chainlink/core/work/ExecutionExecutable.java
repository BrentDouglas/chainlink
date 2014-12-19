package io.machinecode.chainlink.core.work;

import io.machinecode.chainlink.core.then.ResolvedChain;
import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.work.ExecutionWork;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class ExecutionExecutable extends ExecutableImpl<ExecutionWork> {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(ExecutionExecutable.class);

    public ExecutionExecutable(final ExecutableId parentId, final ExecutionWork work, final ExecutionContext context, final ExecutionRepositoryId executionRepositoryId, final WorkerId workerId) {
        super(parentId, context, work, executionRepositoryId, workerId);
    }

    @Override
    public void doExecute(final RuntimeConfiguration configuration, final Chain<?> chain, final WorkerId workerId, final ExecutableId parentId,
                                 final ExecutionContext childContext) throws Throwable {
        final Registry registry = configuration.getRegistry();
        try {
            final Chain<?> next;
            //TODO Can link after cancel?
            if (chain.isCancelled()) {
                this.context.getJobContext().setBatchStatus(BatchStatus.STOPPING);
                final Executable parent = registry.getExecutableAndContext(context.getJobExecutionId(), parentId)
                        .getExecutable();
                next = configuration.getExecutor().callback(parent, this.context);
            } else {
                final ExecutableId callbackId = registry.generateExecutableId();
                registry.registerExecutableAndContext(context.getJobExecutionId(), new ExecutionCallback(callbackId, parentId, this, workerId), context);
                next = work.before(configuration, this.executionRepositoryId, workerId, callbackId, parentId, this.context);
            }
            chain.link(next != null ? next : new ResolvedChain<Void>(null));
            chain.resolve(null);
        } catch (final Throwable e) {
            log.errorf(e, Messages.format("CHAINLINK-023000.work.execution.before.exception", this.context));
            if (this.context.getStepContext() != null) {
                this.context.getStepContext().setBatchStatus(BatchStatus.FAILED);
            }
            this.context.getJobContext().setBatchStatus(BatchStatus.FAILED);
            final Executable parent = registry.getExecutableAndContext(context.getJobExecutionId(), parentId)
                    .getExecutable();
            final Chain<?> next = configuration.getExecutor().callback(parent, this.context);
            chain.link(next != null ? next : new ResolvedChain<Void>(null));
            chain.reject(e);
        }
    }

    @Override
    protected Logger log() {
        return log;
    }
}
