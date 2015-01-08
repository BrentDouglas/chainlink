package io.machinecode.chainlink.transport.infinispan.callable;

import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.transport.infinispan.InfinispanTransport;
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
        final InfinispanTransport transport = cache.getCacheManager().getGlobalComponentRegistry().getComponent(InfinispanTransport.class);
        final ExecutionRepository repository = transport.getRegistry().getExecutionRepository(id);
        if (repository != null) {
            return transport.getLocal();
        }
        return null;
    }
}
