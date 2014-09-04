package io.machinecode.chainlink.transport.infinispan.callable;

import io.machinecode.chainlink.spi.registry.ExecutableAndContext;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.transport.infinispan.InfinispanRegistry;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FindExecutableAndContextCallable extends BaseCallable<Object, Object, ExecutableAndContext> {

    final long jobExecutableId;
    final ExecutableId id;

    public FindExecutableAndContextCallable(final long jobExecutableId, final ExecutableId id) {
        this.jobExecutableId = jobExecutableId;
        this.id = id;
    }

    @Override
    public ExecutableAndContext call() throws Exception {
        final InfinispanRegistry registry = cache.getCacheManager().getGlobalComponentRegistry().getComponent(InfinispanRegistry.class);
        return registry.getExecutableAndContext(jobExecutableId, id);
    }
}
