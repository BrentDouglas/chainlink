package io.machinecode.chainlink.transport.infinispan.callable;

import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.transport.infinispan.InfinispanRegistry;
import org.infinispan.remoting.transport.Address;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FindWorkerCallable extends BaseCallable<Object, Object, Address> {

    final WorkerId id;

    public FindWorkerCallable(final WorkerId id) {
        this.id = id;
    }

    @Override
    public Address call() throws Exception {
        final InfinispanRegistry registry = cache.getCacheManager().getGlobalComponentRegistry().getComponent(InfinispanRegistry.class);
        return registry.hasWorker(id) ? registry.getLocal() : null;
    }
}
