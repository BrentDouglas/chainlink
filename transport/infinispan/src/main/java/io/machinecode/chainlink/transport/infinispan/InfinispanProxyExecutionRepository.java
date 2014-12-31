package io.machinecode.chainlink.transport.infinispan;

import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.chainlink.core.transport.DistributedProxyExecutionRepository;
import io.machinecode.chainlink.core.transport.cmd.InvokeExecutionRepositoryCommand;
import org.infinispan.remoting.transport.Address;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class InfinispanProxyExecutionRepository extends DistributedProxyExecutionRepository<Address> {

    public InfinispanProxyExecutionRepository(final Transport<Address> registry, final ExecutionRepositoryId executionRepositoryId, final Address address) {
        super(registry, executionRepositoryId, address);
    }

    @Override
    protected <T> Command<T, Address> _cmd(final String name, final Serializable... params) {
        return new InvokeExecutionRepositoryCommand<>(executionRepositoryId, name, params);
    }
}
