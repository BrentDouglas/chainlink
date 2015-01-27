package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.then.Chain;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface RemoteExecution {

    Worker getWorker();

    ChainId getLocalId();

    ChainId getRemoteId();

    Chain<?> getChain();
}
