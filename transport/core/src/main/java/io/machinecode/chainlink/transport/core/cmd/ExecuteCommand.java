package io.machinecode.chainlink.transport.core.cmd;

import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.transport.core.DistributedRegistry;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class ExecuteCommand<A,R extends DistributedRegistry<A,R>> implements DistributedCommand<Object,A,R> {

    final WorkerId workerId;
    final ExecutableEvent event;

    public ExecuteCommand(final WorkerId workerId, final ExecutableEvent event) {
        this.workerId = workerId;
        this.event = event;
    }

    @Override
    public Object perform(final R registry, final A origin) throws Throwable {
        registry.getWorker(workerId).execute(event);
        return null;
    }
}
