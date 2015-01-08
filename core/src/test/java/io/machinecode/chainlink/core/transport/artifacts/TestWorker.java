package io.machinecode.chainlink.core.transport.artifacts;

import io.machinecode.chainlink.core.transport.DistributedWorker;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.spi.transport.Transport;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public class TestWorker extends DistributedWorker<String> {

    public TestWorker(final Transport<String> transport, final String local, final String remote, final WorkerId workerId) {
        super(transport, local, remote, workerId);
    }

    @Override
    protected Command<ChainId, String> createPushChainCommand(final long jobExecutionId, final ChainId localId) {
        return new TestPushCommand(jobExecutionId, localId);
    }

    @Override
    protected Chain<?> createLocalChain(final long jobExecutionId, final ChainId remoteId) {
        return new TestLocalChain(transport, remote, jobExecutionId, remoteId);
    }
}
