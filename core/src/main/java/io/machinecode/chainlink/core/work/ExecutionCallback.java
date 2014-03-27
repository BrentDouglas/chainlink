package io.machinecode.chainlink.core.work;

import io.machinecode.chainlink.core.deferred.ResolvedDeferred;
import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.transport.ExecutableId;
import io.machinecode.chainlink.spi.transport.WorkerId;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.work.ExecutionWork;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class ExecutionCallback extends ExecutableImpl<ExecutionWork> implements Serializable {

    private static final Logger log = Logger.getLogger(ExecutionCallback.class);

    public ExecutionCallback(final ExecutableId parentId, final ExecutableImpl<ExecutionWork> executable, final WorkerId workerId) {
        super(parentId, executable, workerId);
    }

    @Override
    public void doExecute(final RuntimeConfiguration configuration, final Deferred<?> deferred, final WorkerId workerId,
                                 final ExecutableId parentId, final ExecutionContext childContext) throws Throwable {
        Deferred<?> next;
        try{
            if (deferred.isCancelled()) {
                this.context.getJobContext().setBatchStatus(BatchStatus.STOPPING);
            }
            next = work.after(configuration, this.executionRepositoryId, workerId, parentId, this.context, childContext);
            deferred.link(next != null ? next : new ResolvedDeferred<Void>(null));
            deferred.resolve(null);
        } catch (final Throwable e) {
            log.errorf(e, Messages.format("CHAINLINK-023001.work.execution.after.exception", this.context));
            if (this.context.getStepContext() != null) {
                this.context.getStepContext().setBatchStatus(BatchStatus.FAILED);
            }
            this.context.getJobContext().setBatchStatus(BatchStatus.FAILED);
            final Executable callback = configuration.getTransport().getExecutable(context.getJobExecutionId(), parentId);
            next = configuration.getExecutor().callback(callback, this.context);
            deferred.link(next != null ? next : new ResolvedDeferred<Void>(null));
            deferred.reject(e);
        }
    }

    @Override
    protected Logger log() {
        return log;
    }
}
