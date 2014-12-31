package io.machinecode.chainlink.transport.infinispan.callable;

import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.core.transport.WorkerIdAndAddress;
import io.machinecode.chainlink.transport.infinispan.InfinispanTransport;
import org.infinispan.remoting.transport.Address;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class FindWorkerForExecutionCallable extends BaseCallable<Object, Object, WorkerIdAndAddress<Address>> {
    private static final long serialVersionUID = 1L;

    final long jobExecutionId;
    final ExecutableId executableId;

    public FindWorkerForExecutionCallable(final long jobExecutionId, final ExecutableId executableId) {
        this.jobExecutionId = jobExecutionId;
        this.executableId = executableId;
    }

    @Override
    public WorkerIdAndAddress<Address> call() throws Exception {
        final InfinispanTransport transport = cache.getCacheManager().getGlobalComponentRegistry().getComponent(InfinispanTransport.class);
        return transport.hasWorker(jobExecutionId, executableId)
                ? new WorkerIdAndAddress<>(
                        transport.getWorker(jobExecutionId, executableId).id(),
                        transport.getLocal()
                )
                : null;
    }

}
