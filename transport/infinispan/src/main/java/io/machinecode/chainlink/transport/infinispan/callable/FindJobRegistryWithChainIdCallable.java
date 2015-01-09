package io.machinecode.chainlink.transport.infinispan.callable;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.transport.infinispan.InfinispanTransport;
import org.infinispan.factories.GlobalComponentRegistry;
import org.infinispan.remoting.transport.Address;

import javax.batch.operations.JobExecutionNotRunningException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FindJobRegistryWithChainIdCallable extends BaseCallable<Object, Object, Address> {
    private static final long serialVersionUID = 1L;

    final long jobExecutionId;
    final ChainId id;

    public FindJobRegistryWithChainIdCallable(final long jobExecutionId, final ChainId id) {
        this.jobExecutionId = jobExecutionId;
        this.id = id;
    }

    @Override
    public Address call() throws Exception {
        final GlobalComponentRegistry gcr = cache.getCacheManager().getGlobalComponentRegistry();
        final InfinispanTransport transport = gcr.getComponent(InfinispanTransport.class);
        final Registry registry = gcr.getComponent(Registry.class);
        try {
            if (id == null) {
                return transport.getAddress(); //If this search is for the root job
            } else {
                // TODO Can this throw?
                return registry.getChain(jobExecutionId, id) == null ? null : transport.getAddress();
            }
        } catch (final JobExecutionNotRunningException e) {
            //
        }
        return null;
    }
}
