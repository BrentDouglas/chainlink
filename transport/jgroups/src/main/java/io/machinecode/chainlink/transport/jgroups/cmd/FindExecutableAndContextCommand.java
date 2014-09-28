package io.machinecode.chainlink.transport.jgroups.cmd;

import io.machinecode.chainlink.spi.registry.ExecutableAndContext;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.transport.jgroups.JGroupsRegistry;
import org.jgroups.Address;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FindExecutableAndContextCommand implements Command<ExecutableAndContext> {

    final long jobExecutionId;
    final ExecutableId id;

    public FindExecutableAndContextCommand(final long jobExecutionId, final ExecutableId id) {
        this.jobExecutionId = jobExecutionId;
        this.id = id;
    }

    @Override
    public ExecutableAndContext invoke(final JGroupsRegistry registry, final Address origin) throws Throwable {
        return registry.getExecutableAndContext(jobExecutionId, id);
    }
}
