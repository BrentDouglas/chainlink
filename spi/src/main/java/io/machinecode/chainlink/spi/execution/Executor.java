package io.machinecode.chainlink.spi.execution;

import io.machinecode.chainlink.spi.Lifecycle;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.deferred.Deferred;

import javax.batch.operations.JobExecutionNotRunningException;
import javax.transaction.TransactionManager;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Executor extends Lifecycle {

    Deferred<?> execute(final long jobExecutionId, final Executable executable);

    Deferred<?> execute(final Executable executable);

    Deferred<?> distribute(final int maxThreads, final Executable... executables);

    Deferred<?> callback(final Executable executable, final ExecutionContext context);

    Future<?> cancel(final Deferred<?> deferred);
}
