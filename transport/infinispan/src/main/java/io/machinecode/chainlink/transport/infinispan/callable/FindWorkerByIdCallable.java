package io.machinecode.chainlink.transport.infinispan.callable;

import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.transport.infinispan.InfinispanTransport;
import org.infinispan.remoting.transport.Address;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class FindWorkerByIdCallable extends BaseCallable<Object, Object, Address> {
    private static final long serialVersionUID = 1L;

    final WorkerId id;

    public FindWorkerByIdCallable(final WorkerId id) {
        this.id = id;
    }

    @Override
    public Address call() throws Exception {
        final InfinispanTransport registry = cache.getCacheManager().getGlobalComponentRegistry().getComponent(InfinispanTransport.class);
        return registry.hasWorker(id) ? registry.getLocal() : null;
    }
}
