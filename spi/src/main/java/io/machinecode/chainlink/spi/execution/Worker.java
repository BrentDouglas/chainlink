package io.machinecode.chainlink.spi.execution;

import io.machinecode.chainlink.spi.Lifecycle;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.then.api.Promise;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Worker extends Runnable, Lifecycle {

    WorkerId id();

    void execute(final ExecutableEvent event);

    Promise<ChainAndId,Throwable> chain(final Executable executable);

    public class ChainAndId {
        private final ChainId localId;
        private final ChainId remoteId;
        private final Chain<?> chain;

        public ChainAndId(final ChainId localId, final ChainId remoteId, final Chain<?> chain) {
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
}
