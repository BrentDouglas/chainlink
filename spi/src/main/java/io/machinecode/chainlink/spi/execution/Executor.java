package io.machinecode.chainlink.spi.execution;

import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.then.Chain;

import java.util.concurrent.Future;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Executor extends AutoCloseable {

    Chain<?> execute(final long jobExecutionId, final Executable executable);

    Chain<?> execute(final Executable executable);

    Chain<?> distribute(final int maxThreads, final Executable... executables);

    Chain<?> callback(final Executable executable, final ExecutionContext context);

    Future<?> cancel(final Future<?> promise);
}
