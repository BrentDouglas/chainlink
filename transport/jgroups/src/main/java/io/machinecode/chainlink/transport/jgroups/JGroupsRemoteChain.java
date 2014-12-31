package io.machinecode.chainlink.transport.jgroups;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.chainlink.core.transport.DistributedRemoteChain;
import io.machinecode.chainlink.core.transport.cmd.InvokeChainCommand;
import org.jgroups.Address;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JGroupsRemoteChain extends DistributedRemoteChain<Address> {

    public JGroupsRemoteChain(final Transport<Address> transport, final Address address, final long jobExecutionId, final ChainId chainId) {
        super(transport, address, jobExecutionId, chainId);
    }

    @Override
    protected <T> InvokeChainCommand<T,Address> command(final String name, final Serializable... params) {
        return new InvokeChainCommand<>(jobExecutionId, chainId, name, params);
    }
}
