package io.machinecode.chainlink.transport.jgroups;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.transport.core.DistributedWorker;
import io.machinecode.chainlink.transport.jgroups.cmd.JGroupsPushChainCommand;
import org.jgroups.Address;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JGroupsWorker extends DistributedWorker<Address> {

    public JGroupsWorker(final JGroupsTransport registry, final Address local, final Address remote, final WorkerId workerId) {
        super(registry, local, remote, workerId);
    }

    @Override
    protected Command<ChainId, Address> createPushChainCommand(final long jobExecutionId, final ChainId localId) {
        return new JGroupsPushChainCommand(jobExecutionId, localId);
    }

    @Override
    protected Chain<?> createLocalChain(final long jobExecutionId, final ChainId remoteId) {
        return new JGroupsLocalChain(transport, remote, jobExecutionId, remoteId);
    }
}
