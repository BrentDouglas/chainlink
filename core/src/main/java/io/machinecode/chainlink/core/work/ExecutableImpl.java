package io.machinecode.chainlink.core.work;

import io.machinecode.chainlink.core.deferred.LinkedDeferred;
import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.transport.ExecutableId;
import io.machinecode.chainlink.spi.transport.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.transport.WorkerId;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.work.Work;
import org.jboss.logging.Logger;

import java.io.Serializable;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public abstract class ExecutableImpl<T extends Work> implements Executable, Serializable {

    protected final T work;
    protected final WorkerId workerId;
    protected final ExecutionRepositoryId executionRepositoryId;
    protected final ExecutableId parentId;
    protected final ExecutionContext context;

    public ExecutableImpl(final ExecutableId parentId, final ExecutionContext context,
                          final T work, final ExecutionRepositoryId executionRepositoryId, final WorkerId workerId) {
        this.parentId = parentId;
        this.context = context;
        this.work = work;
        this.workerId = workerId;
        this.executionRepositoryId = executionRepositoryId;
    }

    public ExecutableImpl(final ExecutableId parentId, final ExecutableImpl<T> executable, final WorkerId workerId) {
        this(parentId, executable.getContext(), executable.work, executable.executionRepositoryId, workerId);
    }

    @Override
    public ExecutableId getParentId() {
        return parentId;
    }

    @Override
    public ExecutionContext getContext() {
        return context;
    }

    @Override
    public WorkerId getWorkerId() {
        return workerId;
    }

    @Override
    public ExecutionRepositoryId getExecutionRepositoryId() {
        return executionRepositoryId;
    }

    @Override
    public void execute(final RuntimeConfiguration configuration, final Deferred<?> deferred, final WorkerId workerId, final ExecutionContext childContext) {
        try {
            log().tracef(Messages.get("CHAINLINK-015703.executable.execute"), this.context, this);
            doExecute(configuration, deferred, workerId, this.parentId, childContext);
        } catch (final Throwable e) {
            log().errorf(e, Messages.get("CHAINLINK-015704.executable.exception"), this.context, this);
            deferred.reject(e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[workerId=" + workerId + ",work=" + work + "]";
    }

    public class Delegate extends LinkedDeferred<Deferred<?>> {

        @Override
        protected String getResolveLogMessage() {
            return Messages.format("CHAINLINK-015100.executable.resolve", ExecutableImpl.this.context, this);
        }

        @Override
        protected String getRejectLogMessage() {
            return Messages.format("CHAINLINK-015101.executable.reject", ExecutableImpl.this.context, this);
        }

        @Override
        protected String getCancelLogMessage() {
            return Messages.format("CHAINLINK-015102.executable.cancel", ExecutableImpl.this.context, this);
        }

        @Override
        protected String getTimeoutExceptionMessage() {
            return Messages.format("CHAINLINK-015000.executable.timeout", ExecutableImpl.this.context, this);
        }

        @Override
        protected Logger log() {
            return ExecutableImpl.this.log();
        }
    }

    protected abstract void doExecute(final RuntimeConfiguration configuration, final Deferred<?> deferred, final WorkerId workerId,
                                      final ExecutableId parentId, final ExecutionContext context) throws Throwable;

    protected abstract Logger log();
}
