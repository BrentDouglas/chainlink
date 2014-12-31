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
    public Executable perform(final Transport<A> registry, final A origin) throws Throwable {
        return registry.getExecutable(jobExecutionId, id);
    }
}
