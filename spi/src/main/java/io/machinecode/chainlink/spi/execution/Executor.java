package io.machinecode.chainlink.spi.execution;

import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.ThreadId;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.deferred.Deferred;

import javax.batch.operations.JobExecutionNotRunningException;
import javax.transaction.TransactionManager;
import java.util.concurrent.Future;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Executor {

    TransactionManager getTransactionManager();

    ExecutionRepository getRepository();

    InjectionContext getInjectionContext();

    Deferred<?> getJob(long jobExecutionId) throws JobExecutionNotRunningException;

    Deferred<?> removeJob(long jobExecutionId) throws JobExecutionNotRunningException;

    Deferred<?> execute(final long jobExecutionId, final Executable executable);

    Deferred<?> execute(final Executable executable);

    Deferred<?> execute(final int maxThreads, final Executable... executables);

    Deferred<?> callback(final Executable executable, final ExecutionContext context);

    Worker getWorker(final ThreadId threadId);

    Future<?> cancel(Deferred<?> deferred);
}
