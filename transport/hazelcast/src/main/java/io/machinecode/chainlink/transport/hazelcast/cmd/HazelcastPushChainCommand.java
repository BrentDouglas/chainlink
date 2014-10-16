package io.machinecode.chainlink.transport.hazelcast.cmd;

import com.hazelcast.core.Member;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.transport.core.DistributedRemoteChain;
import io.machinecode.chainlink.transport.core.cmd.DistributedCommand;
import io.machinecode.chainlink.transport.core.cmd.PushChainCommand;
import io.machinecode.chainlink.transport.hazelcast.HazelcastRegistry;
import io.machinecode.chainlink.transport.hazelcast.HazelcastRemoteChain;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class HazelcastPushChainCommand extends PushChainCommand<Member,HazelcastRegistry> implements DistributedCommand<ChainId,Member,HazelcastRegistry> {

    public HazelcastPushChainCommand(final long jobExecutionId, final ChainId chainId) {
        super(jobExecutionId, chainId);
    }

    @Override
    protected DistributedRemoteChain<Member, HazelcastRegistry> createRemoteChain(final HazelcastRegistry registry, final Member address) {
        return new HazelcastRemoteChain(registry, address, jobExecutionId, chainId);
    }
}
