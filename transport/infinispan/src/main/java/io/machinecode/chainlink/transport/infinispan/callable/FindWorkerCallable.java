package io.machinecode.chainlink.transport.infinispan.callable;

import io.machinecode.chainlink.transport.infinispan.InfinispanTransport;
import io.machinecode.chainlink.spi.transport.WorkerId;
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
        final InfinispanTransport executor = cache.getCacheManager().getGlobalComponentRegistry().getComponent(InfinispanTransport.class);
        return executor.hasWorker(id) ? executor.getLocal() : null;
    }
}
