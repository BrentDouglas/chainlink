package io.machinecode.chainlink.spi.execution;

import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.then.Chain;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Executable extends Serializable {

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

    void execute(final RuntimeConfiguration configuration, final Chain<?> chain, final WorkerId workerId, final ExecutionContext childContext);
}