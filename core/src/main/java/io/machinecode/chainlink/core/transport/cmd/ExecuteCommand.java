package io.machinecode.chainlink.core.transport.cmd;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import io.machinecode.chainlink.spi.execution.WorkerId;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ExecuteCommand implements Command<Object> {
    private static final long serialVersionUID = 1L;

    final WorkerId workerId;
    final ExecutableEvent event;

    public ExecuteCommand(final WorkerId workerId, final ExecutableEvent event) {
        this.workerId = workerId;
        this.event = event;
    }

    @Override
    public Object perform(final Configuration configuration, final Object origin) throws Throwable {
        configuration.getExecutor().getWorker(workerId).execute(event);
        return null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ExecuteCommand{");
        sb.append("workerId=").append(workerId);
        sb.append(", event=").append(event);
        sb.append('}');
        return sb.toString();
    }
}
