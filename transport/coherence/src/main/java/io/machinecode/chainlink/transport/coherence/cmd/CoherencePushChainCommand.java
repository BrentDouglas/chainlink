package io.machinecode.chainlink.transport.coherence.cmd;

import com.tangosol.net.Member;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.transport.coherence.CoherenceRegistry;
import io.machinecode.chainlink.transport.coherence.CoherenceRemoteChain;
import io.machinecode.chainlink.transport.core.DistributedRemoteChain;
import io.machinecode.chainlink.transport.core.cmd.DistributedCommand;
import io.machinecode.chainlink.transport.core.cmd.PushChainCommand;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class CoherencePushChainCommand extends PushChainCommand<Member,CoherenceRegistry> implements DistributedCommand<ChainId,Member,CoherenceRegistry> {

    public CoherencePushChainCommand(final long jobExecutionId, final ChainId chainId) {
        super(jobExecutionId, chainId);
    }

    @Override
    protected DistributedRemoteChain<Member, CoherenceRegistry> createRemoteChain(final CoherenceRegistry registry, final Member address) {
        return new CoherenceRemoteChain(registry, address, jobExecutionId, chainId);
    }
}
