package io.machinecode.chainlink.transport.infinispan.callable;

import io.machinecode.chainlink.transport.infinispan.InfinispanRegistry;
import io.machinecode.chainlink.transport.infinispan.InfinispanWorkerId;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class LeastBusyWorkerCallable extends BaseCallable<Object, Object, InfinispanWorkerId> {

    @Override
    public InfinispanWorkerId call() throws Exception {
        final InfinispanRegistry registry = cache.getCacheManager().getGlobalComponentRegistry().getComponent(InfinispanRegistry.class);
        return registry.leastBusyWorker();
    }
}
