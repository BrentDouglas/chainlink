package io.machinecode.chainlink.transport.jgroups.cmd;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.transport.core.cmd.DistributedCommand;
import io.machinecode.chainlink.transport.core.DistributedRemoteChain;
import io.machinecode.chainlink.transport.core.cmd.PushChainCommand;
import io.machinecode.chainlink.transport.jgroups.JGroupsRegistry;
import io.machinecode.chainlink.transport.jgroups.JGroupsRemoteChain;
import org.jgroups.Address;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JGroupsPushChainCommand extends PushChainCommand<Address,JGroupsRegistry> implements DistributedCommand<ChainId,Address,JGroupsRegistry> {

    public JGroupsPushChainCommand(final long jobExecutionId, final ChainId chainId) {
        super(jobExecutionId, chainId);
    }

    @Override
    protected DistributedRemoteChain<Address, JGroupsRegistry> createRemoteChain(final JGroupsRegistry registry, final Address address) {
        return new JGroupsRemoteChain(registry, address, jobExecutionId, chainId);
    }
}