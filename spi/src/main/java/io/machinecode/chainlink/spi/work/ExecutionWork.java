package io.machinecode.chainlink.spi.work;

import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.ThreadId;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.element.execution.Execution;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.Executor;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExecutionWork extends Execution, Work, Serializable {

    Deferred<?> before(final Executor executor, final ThreadId threadId, final Executable thisExecutable,
                       final Executable parentExecutable, final ExecutionContext context) throws Exception;

    Deferred<?> after(final Executor executor, final ThreadId threadId, final Executable callback,
                      final ExecutionContext context, final ExecutionContext childContext) throws Exception;
}
