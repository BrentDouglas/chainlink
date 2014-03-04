package io.machinecode.chainlink.spi.execution;

import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.ThreadId;
import io.machinecode.chainlink.spi.deferred.Deferred;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Executable extends Deferred<Deferred<?>>, Serializable {

    Executable getParent();

    ExecutionContext getContext();

    ThreadId getThreadId();

    void execute(final Executor executor, final ThreadId threadId, final Executable callback,
                 final ExecutionContext context);
}