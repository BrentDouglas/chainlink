package io.machinecode.chainlink.core.transport.cmd;

import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.spi.transport.Transport;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FindExecutionRepositoryWithIdCommand<A> implements Command<A,A> {
    private static final long serialVersionUID = 1L;

    final ExecutionRepositoryId executionRepositoryId;

    public FindExecutionRepositoryWithIdCommand(final ExecutionRepositoryId executionRepositoryId) {
        this.executionRepositoryId = executionRepositoryId;
    }

    @Override
    public A perform(final Transport<A> transport, final A origin) throws Throwable {
        final ExecutionRepository repository = transport.getRegistry().getExecutionRepository(executionRepositoryId);
        if (repository != null) {
            return transport.getLocal();
        }
        return null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FindExecutionRepositoryWithIdCommand{");
        sb.append("executionRepositoryId=").append(executionRepositoryId);
        sb.append('}');
        return sb.toString();
    }
}
