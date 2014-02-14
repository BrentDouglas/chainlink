package io.machinecode.nock.spi.execution;

import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.context.ThreadId;
import io.machinecode.nock.spi.deferred.Deferred;

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