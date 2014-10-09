package io.machinecode.chainlink.transport.hazelcast;

import com.hazelcast.core.Member;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.chainlink.transport.core.DistributedRemoteChain;
import io.machinecode.chainlink.transport.core.cmd.InvokeChainCommand;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class HazelcastRemoteChain extends DistributedRemoteChain<Member> {

    public HazelcastRemoteChain(final Transport<Member> transport, final Member address, final long jobExecutionId, final ChainId chainId) {
        super(transport, address, jobExecutionId, chainId);
    }

    @Override
    protected <T> InvokeChainCommand<T,Member> command(final String name, final Serializable... params) {
        return new InvokeChainCommand<>(jobExecutionId, chainId, name, params);
    }
}
