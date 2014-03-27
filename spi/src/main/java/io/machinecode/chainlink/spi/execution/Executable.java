package io.machinecode.chainlink.spi.execution;

import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.transport.ExecutableId;
import io.machinecode.chainlink.spi.transport.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.transport.WorkerId;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Executable extends Serializable {

    ExecutionContext getContext();

    ExecutableId getParentId();

    WorkerId getWorkerId();

    ExecutionRepositoryId getExecutionRepositoryId();

    void execute(final RuntimeConfiguration configuration, final Deferred<?> deferred, final WorkerId workerId, final ExecutionContext childContext);
}