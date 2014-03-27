package io.machinecode.chainlink.transport.infinispan.callable;

import io.machinecode.chainlink.transport.infinispan.InfinispanTransport;
import io.machinecode.chainlink.spi.transport.ExecutionRepositoryId;
import org.infinispan.remoting.transport.Address;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FindExecutionRepositoryCallable extends BaseCallable<Object, Object, Address> {

    final ExecutionRepositoryId id;

    public FindExecutionRepositoryCallable(final ExecutionRepositoryId id) {
        this.id = id;
    }

    @Override
    public Address call() throws Exception {
        final InfinispanTransport executor = cache.getCacheManager().getGlobalComponentRegistry().getComponent(InfinispanTransport.class);
        return executor.hasExecutionRepository(id) ? executor.getLocal() : null;
    }
}
