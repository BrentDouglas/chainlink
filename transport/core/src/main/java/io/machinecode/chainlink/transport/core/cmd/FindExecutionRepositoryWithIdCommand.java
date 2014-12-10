package io.machinecode.chainlink.transport.core.cmd;

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

    final ExecutionRepositoryId id;

    public FindExecutionRepositoryWithIdCommand(final ExecutionRepositoryId id) {
        this.id = id;
    }

    @Override
    public A perform(final Transport<A> transport, final A origin) throws Throwable {
        final ExecutionRepository repository = transport.getRegistry().getExecutionRepository(id);
        if (repository != null) {
            return transport.getLocal();
        }
        return null;
    }
}
