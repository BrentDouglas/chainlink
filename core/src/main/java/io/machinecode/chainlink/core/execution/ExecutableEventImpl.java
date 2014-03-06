package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.ExecutableEvent;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
class ExecutableEventImpl implements ExecutableEvent {
    final Executable executable;
    final ExecutionContext context;

    ExecutableEventImpl(final Executable executable, final ExecutionContext context) {
        this.executable = executable;
        this.context = context;
    }

    ExecutableEventImpl(final Executable executable) {
        this(executable, null);
    }

    @Override
    public Executable getExecutable() {
        return executable;
    }

    @Override
    public ExecutionContext getContext() {
        return context;
    }
}
