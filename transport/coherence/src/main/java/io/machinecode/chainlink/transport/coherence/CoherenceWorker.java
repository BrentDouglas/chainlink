package io.machinecode.chainlink.transport.coherence;

import com.tangosol.net.Member;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.chainlink.transport.coherence.cmd.CoherencePushChainCommand;
import io.machinecode.chainlink.transport.core.DistributedWorker;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class CoherenceWorker extends DistributedWorker<Member> {

    public CoherenceWorker(final Transport<Member> transport, final Member local, final Member remote, final WorkerId workerId) {
        super(transport, local, remote, workerId);
    }

    @Override
    protected Command<ChainId, Member> createPushChainCommand(final long jobExecutionId, final ChainId localId) {
        return new CoherencePushChainCommand(jobExecutionId, localId);
    }

    @Override
    protected Chain<?> createLocalChain(final long jobExecutionId, final ChainId remoteId) {
        return new CoherenceLocalChain(transport, remote, jobExecutionId, remoteId);
    }
}
