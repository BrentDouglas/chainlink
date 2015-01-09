package io.machinecode.chainlink.transport.infinispan.callable;

import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.transport.infinispan.InfinispanTransport;
import org.infinispan.factories.GlobalComponentRegistry;
import org.infinispan.remoting.transport.Address;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FindExecutionRepositoryWithIdCallable extends BaseCallable<Object, Object, Address> {
    private static final long serialVersionUID = 1L;

    final ExecutionRepositoryId id;

    public FindExecutionRepositoryWithIdCallable(final ExecutionRepositoryId id) {
        this.id = id;
    }

    @Override
    public Address call() throws Exception {
        final GlobalComponentRegistry gcr = cache.getCacheManager().getGlobalComponentRegistry();
        final InfinispanTransport transport = gcr.getComponent(InfinispanTransport.class);
        final Registry registry = gcr.getComponent(Registry.class);
        final ExecutionRepository repository = registry.getExecutionRepository(id);
        if (repository != null) {
            return transport.getAddress();
        }
        return null;
    }
}
