package io.machinecode.chainlink.transport.infinispan.callable;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.transport.infinispan.InfinispanTransport;
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
        final InfinispanTransport transport = cache.getCacheManager().getGlobalComponentRegistry().getComponent(InfinispanTransport.class);
        try {
            if (id == null) {
                return transport.getLocal(); //If this search is for the root job
            } else {
                // TODO Can this throw?
                return transport.getRegistry().getChain(jobExecutionId, id) == null ? null : transport.getLocal();
            }
        } catch (final JobExecutionNotRunningException e) {
            //
        }
        return null;
    }
}
