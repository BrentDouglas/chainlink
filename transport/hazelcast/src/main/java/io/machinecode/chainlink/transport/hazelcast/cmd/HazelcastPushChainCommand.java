package io.machinecode.chainlink.transport.hazelcast.cmd;

import com.hazelcast.core.Member;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.chainlink.core.transport.DistributedRemoteChain;
import io.machinecode.chainlink.core.transport.cmd.PushChainCommand;
import io.machinecode.chainlink.transport.hazelcast.HazelcastRemoteChain;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class HazelcastPushChainCommand extends PushChainCommand<Member> {
    private static final long serialVersionUID = 1L;

    public HazelcastPushChainCommand(final long jobExecutionId, final ChainId chainId) {
        super(jobExecutionId, chainId);
    }

    @Override
    protected DistributedRemoteChain<Member> createRemoteChain(final Transport<Member> transport, final Member address) {
        return new HazelcastRemoteChain(transport, address, jobExecutionId, chainId);
    }
}
