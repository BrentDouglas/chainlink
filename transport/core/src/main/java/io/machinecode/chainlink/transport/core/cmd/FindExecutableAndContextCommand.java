package io.machinecode.chainlink.transport.core.cmd;

import io.machinecode.chainlink.spi.registry.ExecutableAndContext;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.transport.core.DistributedRegistry;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class FindExecutableAndContextCommand<A,R extends DistributedRegistry<A,R>> implements DistributedCommand<ExecutableAndContext,A,R> {
    private static final long serialVersionUID = 1L;

    final long jobExecutionId;
    final ExecutableId id;

    public FindExecutableAndContextCommand(final long jobExecutionId, final ExecutableId id) {
        this.jobExecutionId = jobExecutionId;
        this.id = id;
    }

    @Override
    public ExecutableAndContext perform(final R registry, final A origin) throws Throwable {
        return registry.getExecutableAndContext(jobExecutionId, id);
    }
}
