package io.machinecode.chainlink.core.transport.artifacts;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.then.Chain;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TestExecutable implements Executable {

    final ExecutableId id;

    public TestExecutable(final long id, final String address) {
        this.id = new TestId(id, address);
    }

    @Override
    public ExecutableId getId() {
        return id;
    }

    @Override
    public ExecutableId getParentId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public WorkerId getWorkerId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ExecutionRepositoryId getExecutionRepositoryId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ExecutionContext getContext() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void execute(final Configuration configuration, final Chain<?> chain, final WorkerId workerId, final ExecutionContext childContext) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
