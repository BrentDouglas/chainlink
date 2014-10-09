package io.machinecode.chainlink.transport.gridgain;

import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.transport.core.DistributedProxyExecutionRepository;
import io.machinecode.chainlink.transport.core.cmd.InvokeExecutionRepositoryCommand;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class GridGainProxyExecutionRepository extends DistributedProxyExecutionRepository<UUID> {

    public GridGainProxyExecutionRepository(final GridGainTransport registry, final ExecutionRepositoryId executionRepositoryId, final UUID address) {
        super(registry, executionRepositoryId, address);
    }

    @Override
    protected <T> InvokeExecutionRepositoryCommand<T,UUID> _cmd(final String name, final Serializable... params) {
        return new InvokeExecutionRepositoryCommand<>(executionRepositoryId, name, params);
    }
}
