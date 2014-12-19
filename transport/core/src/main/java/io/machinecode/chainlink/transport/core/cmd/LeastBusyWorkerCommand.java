package io.machinecode.chainlink.transport.core.cmd;

import io.machinecode.chainlink.transport.core.DistributedRegistry;
import io.machinecode.chainlink.transport.core.DistributedWorkerId;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class LeastBusyWorkerCommand<A,R extends DistributedRegistry<A,R>> implements DistributedCommand<DistributedWorkerId<A>,A,R> {
    private static final long serialVersionUID = 1L;

    public LeastBusyWorkerCommand() {
        //
    }

    @Override
    public DistributedWorkerId<A> perform(final R registry, final A origin) throws Throwable {
        return registry.leastBusyWorker();
    }
}
