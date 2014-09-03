package io.machinecode.chainlink.core.work;

import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.work.ExecutionWork;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.core.then.ResolvedChain;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class ExecutionCallback extends ExecutableImpl<ExecutionWork> implements Serializable {

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
        Chain<?> next;
        try{
            if (chain.isCancelled()) {
                this.context.getJobContext().setBatchStatus(BatchStatus.STOPPING);
            }
            next = work.after(configuration, this.executionRepositoryId, workerId, parentId, this.context, childContext);
            chain.link(next != null ? next : new ResolvedChain<Void>(null));
            chain.resolve(null);
        } catch (final Throwable e) {
            log.errorf(e, Messages.format("CHAINLINK-023001.work.execution.after.exception", this.context));
            if (this.context.getStepContext() != null) {
                this.context.getStepContext().setBatchStatus(BatchStatus.FAILED);
            }
            this.context.getJobContext().setBatchStatus(BatchStatus.FAILED);
            final Executable callback = configuration.getRegistry()
                    .getExecutable(context.getJobExecutionId(), parentId);
            next = configuration.getExecutor().callback(callback, this.context);
            chain.link(next != null ? next : new ResolvedChain<Void>(null));
            chain.reject(e);
        }
    }

    @Override
    protected Logger log() {
        return log;
    }
}
