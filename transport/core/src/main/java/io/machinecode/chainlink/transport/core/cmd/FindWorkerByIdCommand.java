package io.machinecode.chainlink.transport.core.cmd;

import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.spi.transport.Transport;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class FindWorkerByIdCommand<A> implements Command<A,A> {
    private static final long serialVersionUID = 1L;

    final WorkerId workerId;

    public FindWorkerByIdCommand(final WorkerId workerId) {
        this.workerId = workerId;
    }

    @Override
    public A perform(final Transport<A> transport, final A origin) throws Throwable {
        return transport.hasWorker(workerId) ? transport.getLocal() : null;
    }
}