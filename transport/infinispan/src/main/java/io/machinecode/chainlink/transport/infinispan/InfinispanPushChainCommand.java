package io.machinecode.chainlink.transport.infinispan;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.chainlink.core.transport.DistributedRemoteChain;
import io.machinecode.chainlink.core.transport.cmd.PushChainCommand;
import org.infinispan.remoting.transport.Address;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class InfinispanPushChainCommand extends PushChainCommand<Address> {
    private static final long serialVersionUID = 1L;

    public InfinispanPushChainCommand(final long jobExecutionId, final ChainId chainId) {
        super(jobExecutionId, chainId);
    }

    @Override
    protected DistributedRemoteChain<Address> createRemoteChain(final Transport<Address> transport, final Address address) {
        return new InfinispanRemoteChain(transport, address, jobExecutionId, chainId);
    }
}
