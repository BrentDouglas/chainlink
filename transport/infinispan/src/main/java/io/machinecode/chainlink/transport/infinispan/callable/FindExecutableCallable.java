package io.machinecode.chainlink.transport.infinispan.callable;

import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.transport.infinispan.InfinispanTransport;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class FindExecutableCallable extends BaseCallable<Object, Object, Executable> {
    private static final long serialVersionUID = 1L;

    final long jobExecutionId;
    final ExecutableId id;

    public FindExecutableCallable(final long jobExecutionId, final ExecutableId id) {
        this.jobExecutionId = jobExecutionId;
        this.id = id;
    }

    @Override
    public Executable call() throws Exception {
        final InfinispanTransport transport = cache.getCacheManager().getGlobalComponentRegistry().getComponent(InfinispanTransport.class);
        return transport.getExecutable(jobExecutionId, id);
    }
}
