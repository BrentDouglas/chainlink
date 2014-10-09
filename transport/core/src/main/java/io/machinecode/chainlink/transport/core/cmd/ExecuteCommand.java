package io.machinecode.chainlink.transport.core.cmd;

import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.spi.transport.Transport;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class ExecuteCommand<A> implements Command<Object,A> {
    private static final long serialVersionUID = 1L;

    final WorkerId workerId;
    final ExecutableEvent event;

    public ExecuteCommand(final WorkerId workerId, final ExecutableEvent event) {
        this.workerId = workerId;
        this.event = event;
    }

    @Override
    public Object perform(final Transport<A> transport, final A origin) throws Throwable {
        transport.getWorker(workerId).execute(event);
        return null;
    }
}
