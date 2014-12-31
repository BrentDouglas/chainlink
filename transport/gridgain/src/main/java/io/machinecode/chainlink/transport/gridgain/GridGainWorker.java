package io.machinecode.chainlink.transport.gridgain;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.core.transport.DistributedWorker;
import io.machinecode.chainlink.transport.gridgain.cmd.GridGainPushChainCommand;

import java.util.UUID;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GridGainWorker extends DistributedWorker<UUID> {

    public GridGainWorker(final GridGainTransport registry, final UUID local, final UUID remote, final WorkerId workerId) {
        super(registry, local, remote, workerId);
    }

    @Override
    protected Command<ChainId, UUID> createPushChainCommand(final long jobExecutionId, final ChainId localId) {
        return new GridGainPushChainCommand(jobExecutionId, localId);
    }

    @Override
    protected Chain<?> createLocalChain(final long jobExecutionId, final ChainId remoteId) {
        return new GridGainLocalChain(transport, remote, jobExecutionId, remoteId);
    }
}
