package io.machinecode.chainlink.core.work;

import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.work.Work;
import io.machinecode.chainlink.spi.then.Chain;
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
    public void execute(final RuntimeConfiguration configuration, final Chain<?> chain, final WorkerId workerId,
                        final ExecutionContext childContext) {
        try {
            log().tracef(Messages.get("CHAINLINK-015700.executable.execute"), this.context, this);
            doExecute(configuration, chain, workerId, this.parentId, childContext);
        } catch (final Throwable e) {
            log().errorf(e, Messages.get("CHAINLINK-015701.executable.exception"), this.context, this);
            chain.reject(e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[workerId=" + workerId + ",work=" + work + "]";
    }

    protected abstract void doExecute(final RuntimeConfiguration configuration, final Chain<?> chain, final WorkerId workerId,
                                      final ExecutableId parentId, final ExecutionContext context) throws Throwable;

    protected abstract Logger log();
}
