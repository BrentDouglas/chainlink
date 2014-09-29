package io.machinecode.chainlink.transport.jgroups;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.transport.core.cmd.DistributedCommand;
import io.machinecode.chainlink.transport.core.DistributedWorker;
import io.machinecode.chainlink.transport.jgroups.cmd.JGroupsPushChainCommand;
import org.jgroups.Address;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JGroupsWorker extends DistributedWorker<Address, JGroupsRegistry> {

    public JGroupsWorker(final JGroupsRegistry registry, final Address local, final Address remote, final WorkerId workerId) {
        super(registry, local, remote, workerId);
    }

    @Override
    protected DistributedCommand<ChainId, Address, JGroupsRegistry> createPushChainCommand(final long jobExecutionId, final ChainId localId) {
        return new JGroupsPushChainCommand(jobExecutionId, localId);
    }

    @Override
    protected Chain<?> createLocalChain(final long jobExecutionId, final ChainId remoteId) {
        return new JGroupsLocalChain(registry, remote, jobExecutionId, remoteId);
    }
}
