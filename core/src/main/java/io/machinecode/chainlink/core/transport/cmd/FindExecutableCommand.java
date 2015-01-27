package io.machinecode.chainlink.core.transport.cmd;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.registry.ExecutableId;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class FindExecutableCommand implements Command<Executable> {
    private static final long serialVersionUID = 1L;

    final long jobExecutionId;
    final ExecutableId id;

    public FindExecutableCommand(final long jobExecutionId, final ExecutableId id) {
        this.jobExecutionId = jobExecutionId;
        this.id = id;
    }

    @Override
    public Executable perform(final Configuration configuration, final Object origin) throws Throwable {
        return configuration.getRegistry().getExecutable(jobExecutionId, id);
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
