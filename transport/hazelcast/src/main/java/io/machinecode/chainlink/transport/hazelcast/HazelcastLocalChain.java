package io.machinecode.chainlink.transport.hazelcast;

import com.hazelcast.core.Member;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.transport.core.DistributedLocalChain;
import io.machinecode.chainlink.transport.core.cmd.InvokeChainCommand;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class HazelcastLocalChain extends DistributedLocalChain<Member,HazelcastRegistry> {

    public HazelcastLocalChain(final HazelcastRegistry registry, final Member address, final long jobExecutionId, final ChainId chainId) {
        super(registry, address, jobExecutionId, chainId);
    }

    @Override
    protected <T> InvokeChainCommand<T,Member,HazelcastRegistry> command(final String name, final Serializable... params) {
        return new InvokeChainCommand<T,Member,HazelcastRegistry>(jobExecutionId, chainId, name, params);
    }
}
