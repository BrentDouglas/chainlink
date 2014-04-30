package io.machinecode.chainlink.transport.infinispan.callable;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.JobRegistry;
import io.machinecode.chainlink.transport.infinispan.InfinispanJobRegistry;
import io.machinecode.chainlink.transport.infinispan.InfinispanRegistry;
import org.infinispan.remoting.transport.Address;

import javax.batch.operations.JobExecutionNotRunningException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FindJobRegistryWithIdCallable extends BaseCallable<Object, Object, Address> {

    final long jobExecutionId;
    final Object id;

    public FindJobRegistryWithIdCallable(final long jobExecutionId, final Object id) {
        this.jobExecutionId = jobExecutionId;
        this.id = id;
    }

    @Override
    public Address call() throws Exception {
        final InfinispanRegistry registry = cache.getCacheManager().getGlobalComponentRegistry().getComponent(InfinispanRegistry.class);
        try {
            final InfinispanJobRegistry jobRegistry = registry.getLocalJobRegistry(jobExecutionId);
            if (id == null) {
                return registry.getLocal(); //If this search is for the root job
            } else if (id instanceof ChainId) {
                return jobRegistry.getLocalChain((ChainId) id) == null ? null : registry.getLocal();
            } else if (id instanceof ExecutableId) {
                return jobRegistry.getLocalExecutable((ExecutableId)id) == null ? null : registry.getLocal();
            }
        } catch (final JobExecutionNotRunningException e) {
            //
        }
        return null;
    }
}
