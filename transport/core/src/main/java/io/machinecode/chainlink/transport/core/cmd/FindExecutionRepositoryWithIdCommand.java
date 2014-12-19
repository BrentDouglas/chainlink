package io.machinecode.chainlink.transport.core.cmd;

import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.transport.core.DistributedRegistry;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class FindExecutionRepositoryWithIdCommand<A,R extends DistributedRegistry<A,R>> implements DistributedCommand<A,A,R> {
    private static final long serialVersionUID = 1L;

    final ExecutionRepositoryId id;

    public FindExecutionRepositoryWithIdCommand(final ExecutionRepositoryId id) {
        this.id = id;
    }

    @Override
    public A perform(final R registry, final A origin) throws Throwable {
        final ExecutionRepository repository = registry.getLocalExecutionRepository(id);
        if (repository != null) {
            return registry.getLocal();
        }
        return null;
    }
}
