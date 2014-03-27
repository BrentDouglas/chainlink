package io.machinecode.chainlink.transport.infinispan.callable;

import io.machinecode.chainlink.transport.infinispan.InfinispanThreadWorkerId;
import io.machinecode.chainlink.transport.infinispan.InfinispanTransport;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class LeastBusyWorkerCallable extends BaseCallable<Object, Object, InfinispanThreadWorkerId> {

    @Override
    public InfinispanThreadWorkerId call() throws Exception {
        final InfinispanTransport transport = cache.getCacheManager().getGlobalComponentRegistry().getComponent(InfinispanTransport.class);
        return transport.leastBusyWorker();
    }
}
