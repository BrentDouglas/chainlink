package io.machinecode.nock.spi.execution;

import io.machinecode.nock.spi.ExecutionRepository;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.context.ThreadId;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.deferred.Deferred;
import io.machinecode.nock.spi.work.JobWork;

import javax.transaction.TransactionManager;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Executor {

    TransactionManager getTransactionManager();

    ExecutionRepository getRepository();

    InjectionContext createInjectionContext(ExecutionContext context);

    Deferred<?,?> getJob(long jobExecutionId);

    Deferred<?,?> removeJob(long jobExecutionId);

    Deferred<?,?> execute(final Executable executable);

    Deferred<?,?> callback(final CallbackExecutable executable, final ExecutionContext context);

    Deferred<?,?> execute(final ThreadId threadId, final Executable executable);

    Deferred<?,?> callback(final ThreadId threadId, final CallbackExecutable executable, final ExecutionContext context);

    Deferred<?,?> execute(final int maxThreads, final Executable... executables);

    Worker getWorker(final ThreadId threadId);
}
