package io.machinecode.chainlink.core.transport.cmd;

import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.spi.transport.Transport;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class FindExecutableCommand<A> implements Command<Executable,A> {
    private static final long serialVersionUID = 1L;

    final long jobExecutionId;
    final ExecutableId id;

    public FindExecutableCommand(final long jobExecutionId, final ExecutableId id) {
        this.jobExecutionId = jobExecutionId;
        this.id = id;
    }

    @Override
    public Executable perform(final Transport<A> transport, final A origin) throws Throwable {
        return transport.getRegistry().getExecutable(jobExecutionId, id);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FindExecutableCommand{");
        sb.append("jobExecutionId=").append(jobExecutionId);
        sb.append(", id=").append(id);
        sb.append('}');
        return sb.toString();
    }
}
