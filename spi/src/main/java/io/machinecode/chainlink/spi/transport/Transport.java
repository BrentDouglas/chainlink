package io.machinecode.chainlink.spi.transport;

import io.machinecode.chainlink.spi.Lifecycle;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.then.api.Promise;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface Transport extends Lifecycle, Addressed {

    Promise<Chain<?>,Throwable,Object> distribute(final int maxThreads, final Executable... executables) throws Exception;

    Promise<Chain<?>,Throwable,Object> callback(final ExecutableId executableId, final ExecutionContext context) throws Exception;

    ExecutionRepository getExecutionRepository(final ExecutionRepositoryId id) throws Exception;

    long getTimeout();

    TimeUnit getTimeUnit();
}
