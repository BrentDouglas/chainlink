package io.machinecode.chainlink.transport.gridgain;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.transport.core.DistributedWorker;
import io.machinecode.chainlink.transport.core.cmd.DistributedCommand;
import io.machinecode.chainlink.transport.gridgain.cmd.GridGainPushChainCommand;

import java.util.UUID;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class GridGainWorker extends DistributedWorker<UUID, GridGainRegistry> {

    public GridGainWorker(final GridGainRegistry registry, final UUID local, final UUID remote, final WorkerId workerId) {
        super(registry, local, remote, workerId);
    }

    @Override
    protected DistributedCommand<ChainId, UUID, GridGainRegistry> createPushChainCommand(final long jobExecutionId, final ChainId localId) {
        return new GridGainPushChainCommand(jobExecutionId, localId);
    }

    @Override
    protected Chain<?> createLocalChain(final long jobExecutionId, final ChainId remoteId) {
        return new GridGainLocalChain(registry, remote, jobExecutionId, remoteId);
    }
}