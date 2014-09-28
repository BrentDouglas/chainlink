package io.machinecode.chainlink.transport.infinispan.callable;

import io.machinecode.chainlink.spi.registry.ExecutableAndContext;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.transport.infinispan.InfinispanRegistry;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FindExecutableAndContextCallable extends BaseCallable<Object, Object, ExecutableAndContext> {

    final long jobExecutionId;
    final ExecutableId id;

    public FindExecutableAndContextCallable(final long jobExecutionId, final ExecutableId id) {
        this.jobExecutionId = jobExecutionId;
        this.id = id;
    }

    @Override
    public ExecutableAndContext call() throws Exception {
        final InfinispanRegistry registry = cache.getCacheManager().getGlobalComponentRegistry().getComponent(InfinispanRegistry.class);
        return registry.getExecutableAndContext(jobExecutionId, id);
    }
}
