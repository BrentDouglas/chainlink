package io.machinecode.chainlink.transport.jgroups;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.transport.core.DistributedLocalChain;
import io.machinecode.chainlink.transport.core.cmd.InvokeChainCommand;
import org.jgroups.Address;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class JGroupsLocalChain extends DistributedLocalChain<Address,JGroupsRegistry> {

    public JGroupsLocalChain(final JGroupsRegistry registry, final Address address, final long jobExecutionId, final ChainId chainId) {
        super(registry, address, jobExecutionId, chainId);
    }

    @Override
    protected <T> InvokeChainCommand<T,Address,JGroupsRegistry> command(final String name, final Serializable... params) {
        return new InvokeChainCommand<T,Address,JGroupsRegistry>(jobExecutionId, chainId, name, params);
    }
}
