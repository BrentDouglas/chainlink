package io.machinecode.chainlink.transport.jgroups.cmd;

import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.transport.jgroups.JGroupsRegistry;
import org.jgroups.Address;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FindExecutionRepositoryWithIdCommand implements Command<Address> {

    final ExecutionRepositoryId id;

    public FindExecutionRepositoryWithIdCommand(final ExecutionRepositoryId id) {
        this.id = id;
    }

    @Override
    public Address invoke(final JGroupsRegistry registry, final Address origin) throws Throwable {
        final ExecutionRepository repository = registry.getLocalExecutionRepository(id);
        if (repository != null) {
            return registry.getLocal();
        }
        return null;
    }
}
