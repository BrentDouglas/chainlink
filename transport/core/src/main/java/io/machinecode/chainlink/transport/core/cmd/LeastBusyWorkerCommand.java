package io.machinecode.chainlink.transport.core.cmd;

import io.machinecode.chainlink.transport.core.DistributedRegistry;
import io.machinecode.chainlink.transport.core.DistributedWorkerId;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class LeastBusyWorkerCommand<A,R extends DistributedRegistry<A,R>> implements DistributedCommand<DistributedWorkerId<A>,A,R> {

    public LeastBusyWorkerCommand() {
        //
    }

    @Override
    public DistributedWorkerId<A> perform(final R registry, final A origin) throws Throwable {
        return registry.leastBusyWorker();
    }
}
