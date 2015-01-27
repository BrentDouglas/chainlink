package io.machinecode.chainlink.core.transport.cmd;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.transport.Transport;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class CleanupCommand implements Command<Void> {
    private static final long serialVersionUID = 1L;

    final long jobExecutionId;

    public CleanupCommand(final long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    @Override
    public Void perform(final Configuration configuration, final Object origin) throws Throwable {
        final Transport transport = configuration.getTransport();
        configuration.getRegistry().unregisterJob(jobExecutionId).get(transport.getTimeout(), transport.getTimeUnit());
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
