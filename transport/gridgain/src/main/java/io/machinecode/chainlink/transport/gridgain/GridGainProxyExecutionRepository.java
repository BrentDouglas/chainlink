package io.machinecode.chainlink.transport.gridgain;

import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.transport.core.DistributedProxyExecutionRepository;
import io.machinecode.chainlink.transport.core.cmd.InvokeExecutionRepositoryCommand;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class GridGainProxyExecutionRepository extends DistributedProxyExecutionRepository<UUID,GridGainRegistry> {

    public GridGainProxyExecutionRepository(final GridGainRegistry registry, final ExecutionRepositoryId executionRepositoryId, final UUID address) {
        super(registry, executionRepositoryId, address);
    }

    @Override
    protected <T> InvokeExecutionRepositoryCommand<T,UUID,GridGainRegistry> _cmd(final String name, final Serializable... params) {
        return new InvokeExecutionRepositoryCommand<T,UUID,GridGainRegistry>(executionRepositoryId, name, params);
    }
}
