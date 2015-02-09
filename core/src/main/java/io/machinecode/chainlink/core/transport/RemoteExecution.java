package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.then.Chain;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class RemoteExecution {
    private final Worker worker;
    private final ChainId localId;
    private final ChainId remoteId;
    private final Chain<?> chain;

    public RemoteExecution(final Worker worker, final ChainId localId, final ChainId remoteId, final Chain<?> chain) {
        this.worker = worker;
        this.localId = localId;
        this.remoteId = remoteId;
        this.chain = chain;
    }

    public Worker getWorker() {
        return worker;
    }

    public ChainId getLocalId() {
        return localId;
    }

    public ChainId getRemoteId() {
        return remoteId;
    }

    public Chain<?> getChain() {
        return chain;
    }
}
