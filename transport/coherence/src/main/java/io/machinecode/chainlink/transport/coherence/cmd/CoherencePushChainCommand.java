package io.machinecode.chainlink.transport.coherence.cmd;

import com.tangosol.net.Member;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.chainlink.transport.coherence.CoherenceRemoteChain;
import io.machinecode.chainlink.transport.core.DistributedRemoteChain;
import io.machinecode.chainlink.transport.core.cmd.PushChainCommand;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class CoherencePushChainCommand extends PushChainCommand<Member> {
    private static final long serialVersionUID = 1L;

    public CoherencePushChainCommand(final long jobExecutionId, final ChainId chainId) {
        super(jobExecutionId, chainId);
    }

    @Override
    protected DistributedRemoteChain<Member> createRemoteChain(final Transport<Member> transport, final Member address) {
        return new CoherenceRemoteChain(transport, address, jobExecutionId, chainId);
    }
}
