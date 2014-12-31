package io.machinecode.chainlink.transport.coherence;

import com.tangosol.net.Member;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.chainlink.core.transport.DistributedLocalChain;
import io.machinecode.chainlink.core.transport.cmd.InvokeChainCommand;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class CoherenceLocalChain extends DistributedLocalChain<Member> {

    public CoherenceLocalChain(final Transport<Member> registry, final Member address, final long jobExecutionId, final ChainId chainId) {
        super(registry, address, jobExecutionId, chainId);
    }

    @Override
    protected <T> InvokeChainCommand<T,Member> command(final String name, final Serializable... params) {
        return new InvokeChainCommand<>(jobExecutionId, chainId, name, params);
    }
}
