package io.machinecode.nock.core.exec;

import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.execution.CallbackExecutable;
import io.machinecode.nock.spi.execution.Executable;
import io.machinecode.nock.spi.execution.ExecutableEvent;
import io.machinecode.nock.spi.deferred.Deferred;
import io.machinecode.nock.spi.execution.Worker;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
class ExecutableEventImpl<T extends Executable> implements ExecutableEvent<T> {
    final T executable;
    final CallbackExecutable parentExecutable;
    final ExecutionContext[] contexts;

    ExecutableEventImpl(final T executable, final CallbackExecutable parentExecutable, final ExecutionContext... contexts) {
        this.executable = executable;
        this.parentExecutable = parentExecutable;
        this.contexts = contexts;
    }

    @Override
    public T getExecutable() {
        return executable;
    }

    @Override
    public CallbackExecutable getParentExecutable() {
        return parentExecutable;
    }

    @Override
    public ExecutionContext[] getContexts() {
        return contexts;
    }
}
