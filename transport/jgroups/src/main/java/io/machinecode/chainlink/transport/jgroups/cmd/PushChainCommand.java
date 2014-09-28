package io.machinecode.chainlink.transport.jgroups.cmd;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.transport.jgroups.JGroupsRegistry;
import io.machinecode.chainlink.transport.jgroups.JGroupsRemoteChain;
import org.jgroups.Address;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PushChainCommand implements Command<ChainId> {

    final long jobExecutionId;
    final ChainId chainId;

    public PushChainCommand(final long jobExecutionId, final ChainId chainId) {
        this.jobExecutionId = jobExecutionId;
        this.chainId = chainId;
    }

    @Override
    public ChainId invoke(final JGroupsRegistry registry, final Address origin) throws Throwable {
        final ChainId remoteId = registry.generateChainId();
        registry.registerChain(jobExecutionId, remoteId, new JGroupsRemoteChain(registry, origin, jobExecutionId, chainId));
        return remoteId;
    }
}
