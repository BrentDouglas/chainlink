package io.machinecode.chainlink.core.transport.artifacts;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.execution.WorkerId;
import io.machinecode.chainlink.spi.then.Chain;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TestExecutable implements Executable {

    final ExecutableId id;
    final TestExecutionContext context;
    WorkerId workerId;

    public TestExecutable(final long id, final String address, final TestExecutionContext context) {
        this.context = context;
        this.id = new TestId(id, address);
    }

    @Override
    public ExecutableId getId() {
        return id;
    }

    @Override
    public ExecutableId getParentId() {
        return null;
    }

    @Override
    public WorkerId getWorkerId() {
        return workerId;
    }

    @Override
    public ExecutionRepositoryId getExecutionRepositoryId() {
        return null;
    }

    @Override
    public ExecutionContext getContext() {
        return context;
    }

    @Override
    public void execute(final Configuration configuration, final Chain<?> chain, final WorkerId workerId, final ExecutionContext childContext) {
        this.workerId = workerId;
    }
}
