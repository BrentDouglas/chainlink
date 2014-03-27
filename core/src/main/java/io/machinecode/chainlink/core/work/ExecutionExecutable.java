package io.machinecode.chainlink.core.work;

import io.machinecode.chainlink.core.deferred.ResolvedDeferred;
import io.machinecode.chainlink.core.execution.UUIDExecutableId;
import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.transport.ExecutableId;
import io.machinecode.chainlink.spi.transport.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.transport.WorkerId;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.work.ExecutionWork;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;
import java.util.UUID;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class ExecutionExecutable extends ExecutableImpl<ExecutionWork> implements Serializable {

    private static final Logger log = Logger.getLogger(ExecutionExecutable.class);

    public ExecutionExecutable(final ExecutableId parentId, final ExecutionWork work, final ExecutionContext context, final ExecutionRepositoryId executionRepositoryId, final WorkerId workerId) {
        super(parentId, context, work, executionRepositoryId, workerId);
    }

    @Override
    public void doExecute(final RuntimeConfiguration configuration, final Deferred<?> deferred, final WorkerId workerId, final ExecutableId parentId,
                                 final ExecutionContext childContext) throws Throwable {
        try {
            final Deferred<?> next;
            //TODO Can link after cancel?
            if (deferred.isCancelled()) {
                this.context.getJobContext().setBatchStatus(BatchStatus.STOPPING);
                final Executable callback = configuration.getTransport().getExecutable(context.getJobExecutionId(), parentId);
                next = configuration.getExecutor().callback(callback, this.context);
            } else {
                final ExecutableId callbackId = new UUIDExecutableId(UUID.randomUUID());
                configuration.getTransport().registerExecutable(context.getJobExecutionId(), callbackId, new ExecutionCallback(parentId, this, workerId));
                next = work.before(configuration, this.executionRepositoryId, workerId, callbackId, parentId, this.context);
            }
            deferred.link(next != null ? next : new ResolvedDeferred<Void>(null));
            deferred.resolve(null);
        } catch (final Throwable e) {
            log.errorf(e, Messages.format("CHAINLINK-023000.work.execution.before.exception", this.context));
            if (this.context.getStepContext() != null) {
                this.context.getStepContext().setBatchStatus(BatchStatus.FAILED);
            }
            this.context.getJobContext().setBatchStatus(BatchStatus.FAILED);
            final Executable callback = configuration.getTransport().getExecutable(context.getJobExecutionId(), parentId);
            final Deferred<?> next = configuration.getExecutor().callback(callback, this.context);
            deferred.link(next != null ? next : new ResolvedDeferred<Void>(null));
            deferred.reject(e);
        }
    }

    @Override
    protected Logger log() {
        return log;
    }
}
