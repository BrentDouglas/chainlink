package io.machinecode.chainlink.transport.infinispan;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.chainlink.transport.core.DistributedWorker;
import org.infinispan.remoting.transport.Address;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class InfinispanWorker extends DistributedWorker<Address> {

    public InfinispanWorker(final Transport<Address> transport, final Address local, final Address remote, final WorkerId workerId) {
        super(transport, local, remote, workerId);
    }

    @Override
    protected Command<ChainId, Address> createPushChainCommand(final long jobExecutionId, final ChainId localId) {
        return new InfinispanPushChainCommand(jobExecutionId, localId);
    }

    @Override
    protected Chain<?> createLocalChain(final long jobExecutionId, final ChainId remoteId) {
        return new InfinispanLocalChain(transport, remote, jobExecutionId, remoteId);
    }
}
