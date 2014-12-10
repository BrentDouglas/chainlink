package io.machinecode.chainlink.transport.core.cmd;

import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.spi.transport.Transport;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class LeastBusyWorkerCommand<A> implements Command<WorkerId,A> {
    private static final long serialVersionUID = 1L;

    public LeastBusyWorkerCommand() {
        //
    }

    @Override
    public WorkerId perform(final Transport<A> transport, final A origin) throws Throwable {
        return transport.leastBusyWorker();
    }
}
