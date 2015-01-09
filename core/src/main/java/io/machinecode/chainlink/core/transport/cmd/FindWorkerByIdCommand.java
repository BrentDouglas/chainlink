package io.machinecode.chainlink.core.transport.cmd;

import io.machinecode.chainlink.spi.registry.Registry;
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
    public A perform(final Transport<A> transport, final Registry registry, final A origin) throws Throwable {
        return transport.hasWorker(workerId) ? transport.getAddress() : null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FindWorkerByIdCommand{");
        sb.append("workerId=").append(workerId);
        sb.append('}');
        return sb.toString();
    }
}
