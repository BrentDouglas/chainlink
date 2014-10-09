package io.machinecode.chainlink.transport.core.cmd;

import io.machinecode.chainlink.spi.execution.CallbackEvent;
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
    public Object perform(final Transport<A> transport, final A origin) throws Throwable {
        transport.getWorker(workerId).callback(event);
        return null;
    }
}
