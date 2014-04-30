package io.machinecode.chainlink.transport.infinispan.callable;

import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.transport.infinispan.InfinispanRegistry;
import org.infinispan.remoting.transport.Address;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FetchExecutableCallable extends BaseCallable<Object, Object, Executable> {

    final long jobExecutionId;
    final ExecutableId executableId;

    public FetchExecutableCallable(final long jobExecutionId, final ExecutableId executableId) {
        this.jobExecutionId = jobExecutionId;
        this.executableId = executableId;
    }

    @Override
    public Executable call() throws Exception {
        final InfinispanRegistry registry = cache.getCacheManager().getGlobalComponentRegistry().getComponent(InfinispanRegistry.class);
        return registry.getJobRegistry(jobExecutionId).getLocalExecutable(executableId);
    }
}
