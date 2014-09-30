package io.machinecode.chainlink.transport.coherence;

import com.tangosol.net.Member;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.transport.core.DistributedRemoteChain;
import io.machinecode.chainlink.transport.core.cmd.InvokeChainCommand;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CoherenceRemoteChain extends DistributedRemoteChain<Member,CoherenceRegistry> {

    public CoherenceRemoteChain(final CoherenceRegistry registry, final Member address, final long jobExecutionId, final ChainId chainId) {
        super(registry, address, jobExecutionId, chainId);
    }

    @Override
    protected <T> InvokeChainCommand<T,Member,CoherenceRegistry> command(final String name, final Serializable... params) {
        return new InvokeChainCommand<T,Member,CoherenceRegistry>(jobExecutionId, chainId, name, params);
    }
}
