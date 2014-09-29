package io.machinecode.chainlink.transport.core.cmd;

import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.transport.core.DistributedRegistry;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FindWorkerCommand<A,R extends DistributedRegistry<A,R>> implements DistributedCommand<A,A,R> {

    final WorkerId workerId;

    public FindWorkerCommand(final WorkerId workerId) {
        this.workerId = workerId;
    }

    @Override
    public A perform(final R registry, final A origin) throws Throwable {
        return registry.hasWorker(workerId) ? registry.getLocal() : null;
    }
}
