package io.machinecode.chainlink.transport.coherence;

import com.tangosol.net.Member;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.chainlink.transport.core.DistributedRemoteChain;
import io.machinecode.chainlink.transport.core.cmd.InvokeChainCommand;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class CoherenceRemoteChain extends DistributedRemoteChain<Member> {

    public CoherenceRemoteChain(final Transport<Member> transport, final Member address, final long jobExecutionId, final ChainId chainId) {
        super(transport, address, jobExecutionId, chainId);
    }

    @Override
    protected <T> InvokeChainCommand<T,Member> command(final String name, final Serializable... params) {
        return new InvokeChainCommand<>(jobExecutionId, chainId, name, params);
    }
}
