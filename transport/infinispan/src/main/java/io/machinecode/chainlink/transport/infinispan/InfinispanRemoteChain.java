package io.machinecode.chainlink.transport.infinispan;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.chainlink.transport.core.DistributedRemoteChain;
import io.machinecode.chainlink.transport.core.cmd.InvokeChainCommand;
import org.infinispan.remoting.transport.Address;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class InfinispanRemoteChain extends DistributedRemoteChain<Address> {

    public InfinispanRemoteChain(final Transport<Address> transport, final Address address, final long jobExecutionId, final ChainId chainId) {
        super(transport, address, jobExecutionId, chainId);
    }

    @Override
    protected <T> InvokeChainCommand<T,Address> command(final String name, final Serializable... params) {
        return new InvokeChainCommand<>(jobExecutionId, chainId, name, params);
    }
}
