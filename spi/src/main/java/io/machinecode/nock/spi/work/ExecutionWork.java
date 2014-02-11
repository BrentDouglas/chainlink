package io.machinecode.nock.spi.work;

import io.machinecode.nock.spi.ExecutionRepository;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.context.ThreadId;
import io.machinecode.nock.spi.deferred.Deferred;
import io.machinecode.nock.spi.element.execution.Execution;
import io.machinecode.nock.spi.execution.CallbackExecutable;
import io.machinecode.nock.spi.execution.Executable;
import io.machinecode.nock.spi.execution.Executor;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExecutionWork extends Execution, Work, Serializable {

    ExecutionContext createExecutionContext(final ExecutionRepository repository, final ExecutionContext parentContext) throws Exception;

    Deferred<?> before(final Executor executor, final ThreadId threadId, final CallbackExecutable thisExecutable,
                         final CallbackExecutable parentExecutable, final ExecutionContext context,
                         final ExecutionContext... contexts) throws Exception;

    Deferred<?> after(final Executor executor, final ThreadId threadId, final CallbackExecutable thisExecutable,
                        final CallbackExecutable parentExecutable, final ExecutionContext context,
                        final ExecutionContext childContext) throws Exception;
}
