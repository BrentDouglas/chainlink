package io.machinecode.chainlink.transport.infinispan.callable;

import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.transport.infinispan.InfinispanRegistry;
import org.infinispan.remoting.transport.Address;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class FindExecutionRepositoryWithIdCallable extends BaseCallable<Object, Object, Address> {

    final ExecutionRepositoryId id;

    public FindExecutionRepositoryWithIdCallable(final ExecutionRepositoryId id) {
        this.id = id;
    }

    @Override
    public Address call() throws Exception {
        final InfinispanRegistry registry = cache.getCacheManager().getGlobalComponentRegistry().getComponent(InfinispanRegistry.class);
        final ExecutionRepository repository = registry.getLocalExecutionRepository(id);
        if (repository != null) {
            return registry.getLocal();
        }
        return null;
    }
}
