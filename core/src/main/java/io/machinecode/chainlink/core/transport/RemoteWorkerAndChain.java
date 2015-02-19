package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.spi.execution.WorkerId;
import io.machinecode.chainlink.spi.registry.ChainId;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class RemoteWorkerAndChain implements Serializable {
    private static final long serialVersionUID = 1L;

    private final WorkerId workerId;
    private final ChainId chainId;

    public RemoteWorkerAndChain(final WorkerId workerId, final ChainId chainId) {
        this.workerId = workerId;
        this.chainId = chainId;
    }

    public WorkerId getWorkerId() {
        return workerId;
    }

    public ChainId getChainId() {
        return chainId;
    }
}
