package io.machinecode.chainlink.transport.jgroups;

import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.transport.core.DistributedExecutionRepositoryProxy;
import io.machinecode.chainlink.transport.core.cmd.InvokeExecutionRepositoryCommand;
import org.jgroups.Address;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JGroupsExecutionRepositoryProxy extends DistributedExecutionRepositoryProxy<Address,JGroupsRegistry> {

    public JGroupsExecutionRepositoryProxy(final JGroupsRegistry registry, final ExecutionRepositoryId executionRepositoryId, final Address address) {
        super(registry, executionRepositoryId, address);
    }

    @Override
    protected <T> InvokeExecutionRepositoryCommand<T,Address,JGroupsRegistry> _cmd(final String name, final Serializable... params) {
        return new InvokeExecutionRepositoryCommand<T,Address,JGroupsRegistry>(executionRepositoryId, name, params);
    }
}
