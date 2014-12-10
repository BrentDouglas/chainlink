package io.machinecode.chainlink.transport.hazelcast;

import com.hazelcast.core.Member;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.transport.core.DistributedWorker;
import io.machinecode.chainlink.transport.hazelcast.cmd.HazelcastPushChainCommand;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class HazelcastWorker extends DistributedWorker<Member> {

    public HazelcastWorker(final HazelcastTransport registry, final Member local, final Member remote, final WorkerId workerId) {
        super(registry, local, remote, workerId);
    }

    @Override
    protected Command<ChainId, Member> createPushChainCommand(final long jobExecutionId, final ChainId localId) {
        return new HazelcastPushChainCommand(jobExecutionId, localId);
    }

    @Override
    protected Chain<?> createLocalChain(final long jobExecutionId, final ChainId remoteId) {
        return new HazelcastLocalChain(transport, remote, jobExecutionId, remoteId);
    }
}
