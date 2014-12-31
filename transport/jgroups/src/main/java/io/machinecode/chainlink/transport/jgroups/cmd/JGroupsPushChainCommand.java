package io.machinecode.chainlink.transport.jgroups.cmd;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.chainlink.core.transport.DistributedRemoteChain;
import io.machinecode.chainlink.core.transport.cmd.PushChainCommand;
import io.machinecode.chainlink.transport.jgroups.JGroupsRemoteChain;
import org.jgroups.Address;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JGroupsPushChainCommand extends PushChainCommand<Address> {
    private static final long serialVersionUID = 1L;

    public JGroupsPushChainCommand(final long jobExecutionId, final ChainId chainId) {
        super(jobExecutionId, chainId);
    }

    @Override
    protected DistributedRemoteChain<Address> createRemoteChain(final Transport<Address> transport, final Address address) {
        return new JGroupsRemoteChain(transport, address, jobExecutionId, chainId);
    }
}
