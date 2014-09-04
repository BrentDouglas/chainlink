package io.machinecode.chainlink.transport.infinispan.callable;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.transport.infinispan.InfinispanRegistry;
import org.infinispan.remoting.transport.Address;

import javax.batch.operations.JobExecutionNotRunningException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FindJobRegistryWithChainIdCallable extends BaseCallable<Object, Object, Address> {

    final long jobExecutionId;
    final ChainId id;

    public FindJobRegistryWithChainIdCallable(final long jobExecutionId, final ChainId id) {
        this.jobExecutionId = jobExecutionId;
        this.id = id;
    }

    @Override
    public Address call() throws Exception {
        final InfinispanRegistry registry = cache.getCacheManager().getGlobalComponentRegistry().getComponent(InfinispanRegistry.class);
        try {
            if (id == null) {
                return registry.getLocal(); //If this search is for the root job
            } else {
                // TODO Can this throw?
                return registry.getChain(jobExecutionId, id) == null ? null : registry.getLocal();
            }
        } catch (final JobExecutionNotRunningException e) {
            //
        }
        return null;
    }
}
