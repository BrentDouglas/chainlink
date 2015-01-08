package io.machinecode.chainlink.core.transport.cmd;

import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.spi.transport.Transport;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class CleanupCommand<A> implements Command<Void,A> {
    private static final long serialVersionUID = 1L;

    final long jobExecutionId;

    public CleanupCommand(final long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    @Override
    public Void perform(final Transport<A> transport, final A origin) throws Throwable {
        transport.getRegistry().unregisterJob(jobExecutionId).get();
        return null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CleanupCommand{");
        sb.append("jobExecutionId=").append(jobExecutionId);
        sb.append('}');
        return sb.toString();
    }
}
