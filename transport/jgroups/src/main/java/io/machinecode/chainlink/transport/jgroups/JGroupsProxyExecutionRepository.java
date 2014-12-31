package io.machinecode.chainlink.transport.jgroups;

import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.core.transport.DistributedProxyExecutionRepository;
import io.machinecode.chainlink.core.transport.cmd.InvokeExecutionRepositoryCommand;
import org.jgroups.Address;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JGroupsProxyExecutionRepository extends DistributedProxyExecutionRepository<Address> {

    public JGroupsProxyExecutionRepository(final JGroupsTransport registry, final ExecutionRepositoryId executionRepositoryId, final Address address) {
        super(registry, executionRepositoryId, address);
    }

    @Override
    protected <T> InvokeExecutionRepositoryCommand<T,Address> _cmd(final String name, final Serializable... params) {
        return new InvokeExecutionRepositoryCommand<>(executionRepositoryId, name, params);
    }
}
