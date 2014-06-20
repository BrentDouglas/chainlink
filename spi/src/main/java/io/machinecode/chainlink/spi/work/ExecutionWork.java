package io.machinecode.chainlink.spi.work;

import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.element.execution.Execution;
import io.machinecode.then.api.Promise;
import io.machinecode.chainlink.spi.then.Chain;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExecutionWork extends Execution, Work, Serializable {

    Chain<?> before(final RuntimeConfiguration configuration, final ExecutionRepositoryId executionRepositoryId,
                       final WorkerId workerId, final ExecutableId callbackId, final ExecutableId parentId,
                       final ExecutionContext context) throws Exception;

    Chain<?> after(final RuntimeConfiguration configuration, final ExecutionRepositoryId executionRepositoryId,
                      final WorkerId workerId, final ExecutableId parentId, final ExecutionContext context,
                      final ExecutionContext childContext) throws Exception;
}
