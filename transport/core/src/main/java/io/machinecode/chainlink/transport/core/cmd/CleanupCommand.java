package io.machinecode.chainlink.transport.core.cmd;

import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.spi.transport.Transport;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
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
}
