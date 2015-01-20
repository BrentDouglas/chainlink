package io.machinecode.chainlink.spi.execution;

import io.machinecode.chainlink.spi.Lifecycle;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.then.Chain;

import java.util.concurrent.Future;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Executor extends Lifecycle {

    Chain<?> execute(final long jobExecutionId, final Executable executable) throws Exception;

    Chain<?> execute(final Executable executable) throws Exception;

    Chain<?> distribute(final int maxThreads, final Executable... executables) throws Exception;

    Chain<?> callback(final ExecutableId executableId, final ExecutionContext context) throws Exception;

    Future<?> cancel(final Future<?> promise) throws Exception;
}
