package io.machinecode.chainlink.core.transport.cmd;

import io.machinecode.chainlink.spi.execution.CallbackEvent;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.spi.transport.Transport;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class CallbackCommand<A> implements Command<Object,A> {
    private static final long serialVersionUID = 1L;

    final WorkerId workerId;
    final CallbackEvent event;

    public CallbackCommand(final WorkerId workerId, final CallbackEvent event) {
        this.workerId = workerId;
        this.event = event;
    }

    @Override
    public Object perform(final Transport<A> transport, final Registry registry, final A origin) throws Throwable {
        transport.getWorker(workerId).callback(event);
        return null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CallbackCommand{");
        sb.append("workerId=").append(workerId);
        sb.append(", event=").append(event);
        sb.append('}');
        return sb.toString();
    }
}
