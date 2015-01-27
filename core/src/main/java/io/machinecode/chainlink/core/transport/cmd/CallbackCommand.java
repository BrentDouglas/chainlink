package io.machinecode.chainlink.core.transport.cmd;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.execution.CallbackEvent;
import io.machinecode.chainlink.spi.execution.WorkerId;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class CallbackCommand implements Command<Object> {
    private static final long serialVersionUID = 1L;

    final WorkerId workerId;
    final CallbackEvent event;

    public CallbackCommand(final WorkerId workerId, final CallbackEvent event) {
        this.workerId = workerId;
        this.event = event;
    }

    @Override
    public Object perform(final Configuration configuration, final Object origin) throws Throwable {
        configuration.getExecutor().getWorker(workerId).callback(event);
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
