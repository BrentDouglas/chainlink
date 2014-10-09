package io.machinecode.chainlink.spi.execution;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.then.Chain;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class ChainAndIds {
    private final ChainId localId;
    private final ChainId remoteId;
    private final Chain<?> chain;

    public ChainAndIds(final ChainId localId, final ChainId remoteId, final Chain<?> chain) {
        this.localId = localId;
        this.remoteId = remoteId;
        this.chain = chain;
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
