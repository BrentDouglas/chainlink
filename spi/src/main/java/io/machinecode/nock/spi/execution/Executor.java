package io.machinecode.nock.spi.execution;

import io.machinecode.nock.spi.ExecutionRepository;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.context.ThreadId;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.deferred.Deferred;

import javax.batch.operations.JobExecutionNotRunningException;
import javax.transaction.TransactionManager;
import java.util.concurrent.Future;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Executor {

    TransactionManager getTransactionManager();

    ExecutionRepository getRepository();

    InjectionContext createInjectionContext(ExecutionContext context);

    Deferred<?> getJob(long jobExecutionId) throws JobExecutionNotRunningException;

    Deferred<?> removeJob(long jobExecutionId) throws JobExecutionNotRunningException;

    Deferred<?> execute(final long jobExecutionId, final Executable executable);

    Deferred<?> execute(final Executable executable);

    Deferred<?> execute(final int maxThreads, final Executable... executables);

    Deferred<?> callback(final Executable executable, final ExecutionContext context);

    Worker getWorker(final ThreadId threadId);

    Future<?> cancel(Deferred<?> deferred);
}
