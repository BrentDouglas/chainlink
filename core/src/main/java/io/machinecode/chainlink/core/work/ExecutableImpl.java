package io.machinecode.chainlink.core.work;

import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.WorkerId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.RepositoryId;
import io.machinecode.chainlink.spi.then.Chain;
import org.jboss.logging.Logger;

import java.io.Serializable;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public abstract class ExecutableImpl<T> implements Executable, Serializable {

    protected final T work;
    protected final WorkerId workerId;
    protected final RepositoryId repositoryId;
    protected final ExecutableId parentId;
    protected final ExecutionContext context;

    public ExecutableImpl(final ExecutableId parentId, final ExecutionContext context,
                          final T work, final RepositoryId repositoryId, final WorkerId workerId) {
        this.parentId = parentId;
        this.context = context;
        this.work = work;
        this.workerId = workerId;
        this.repositoryId = repositoryId;
    }

    public ExecutableImpl(final ExecutableId parentId, final ExecutableImpl<T> executable, final WorkerId workerId) {
        this(parentId, executable.getContext(), executable.work, executable.repositoryId, workerId);
    }

    @Override
    public ExecutableId getId() {
        return null;
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
    public RepositoryId getRepositoryId() {
        return repositoryId;
    }

    @Override
    public void execute(final Configuration configuration, final Chain<?> chain, final WorkerId workerId,
                        final ExecutionContext childContext) {
        try {
            log().tracef(Messages.get("CHAINLINK-015700.executable.execute"), this.context, this);
            doExecute(configuration, chain, workerId, this.parentId, childContext);
        } catch (final Throwable e) {
            log().errorf(e, Messages.get("CHAINLINK-015701.executable.exception"), this.context, this);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[workerId=" + workerId + ",work=" + work + "]";
    }

    protected abstract void doExecute(final Configuration configuration, final Chain<?> chain, final WorkerId workerId,
                                      final ExecutableId parentId, final ExecutionContext context) throws Throwable;

    protected abstract Logger log();
}
