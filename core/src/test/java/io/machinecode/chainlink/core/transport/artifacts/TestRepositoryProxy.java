package io.machinecode.chainlink.core.transport.artifacts;

import io.machinecode.chainlink.core.transport.DistributedProxyExecutionRepository;
import io.machinecode.chainlink.core.transport.cmd.InvokeExecutionRepositoryCommand;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.spi.transport.Transport;

import java.io.Serializable;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public class TestRepositoryProxy extends DistributedProxyExecutionRepository<String> {

    public TestRepositoryProxy(final Transport<String> transport, final ExecutionRepositoryId executionRepositoryId, final String address) {
        super(transport, executionRepositoryId, address);
    }

    @Override
    protected <T> Command<T, String> _cmd(final String name, final Serializable... params) {
        return new InvokeExecutionRepositoryCommand<>(executionRepositoryId, name, params);
    }
}
