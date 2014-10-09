package io.machinecode.chainlink.transport.infinispan.callable;

import io.machinecode.chainlink.transport.infinispan.InfinispanTransport;
import io.machinecode.chainlink.transport.infinispan.InfinispanWorkerId;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class LeastBusyWorkerCallable extends BaseCallable<Object, Object, InfinispanWorkerId> {
    private static final long serialVersionUID = 1L;

    @Override
    public InfinispanWorkerId call() throws Exception {
        final InfinispanTransport registry = cache.getCacheManager().getGlobalComponentRegistry().getComponent(InfinispanTransport.class);
        return registry.leastBusyWorker();
    }
}
