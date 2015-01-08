package io.machinecode.chainlink.spi.execution;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.then.Chain;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Executable {

    /**
     * @return The id of this executable.
     */
    ExecutableId getId();

    /**
     * @return The id of the executable that spawned this executable.
     */
    ExecutableId getParentId();

    WorkerId getWorkerId();

    ExecutionRepositoryId getExecutionRepositoryId();

    ExecutionContext getContext();

    void execute(final Configuration configuration, final Chain<?> chain, final WorkerId workerId, final ExecutionContext childContext);
}