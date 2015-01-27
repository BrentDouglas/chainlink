package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.then.Chain;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class RemoteExecutionImpl implements RemoteExecution {
    private final Worker worker;
    private final ChainId localId;
    private final ChainId remoteId;
    private final Chain<?> chain;

    public RemoteExecutionImpl(final Worker worker, final ChainId localId, final ChainId remoteId, final Chain<?> chain) {
        this.worker = worker;
        this.localId = localId;
        this.remoteId = remoteId;
        this.chain = chain;
    }

    @Override
    public Worker getWorker() {
        return worker;
    }

    @Override
    public ChainId getLocalId() {
        return localId;
    }

    @Override
    public ChainId getRemoteId() {
        return remoteId;
    }

    @Override
    public Chain<?> getChain() {
        return chain;
    }
}
