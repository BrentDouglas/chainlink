package io.machinecode.chainlink.transport.coherence;

import com.tangosol.net.Member;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.transport.coherence.cmd.CoherencePushChainCommand;
import io.machinecode.chainlink.transport.core.DistributedWorker;
import io.machinecode.chainlink.transport.core.cmd.DistributedCommand;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class CoherenceWorker extends DistributedWorker<Member, CoherenceRegistry> {

    public CoherenceWorker(final CoherenceRegistry registry, final Member local, final Member remote, final WorkerId workerId) {
        super(registry, local, remote, workerId);
    }

    @Override
    protected DistributedCommand<ChainId, Member, CoherenceRegistry> createPushChainCommand(final long jobExecutionId, final ChainId localId) {
        return new CoherencePushChainCommand(jobExecutionId, localId);
    }

    @Override
    protected Chain<?> createLocalChain(final long jobExecutionId, final ChainId remoteId) {
        return new CoherenceLocalChain(registry, remote, jobExecutionId, remoteId);
    }
}
