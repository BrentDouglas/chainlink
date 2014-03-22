package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.ExecutableEvent;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class ExecutableEventImpl implements ExecutableEvent {
    final Executable executable;
    final ExecutionContext context;
    final Type type;

    public ExecutableEventImpl(final Executable executable, final ExecutionContext context, final Type type) {
        this.executable = executable;
        this.context = context;
        this.type = type;
    }

    public ExecutableEventImpl(final Executable executable, final Type type) {
        this(executable, null, type);
    }

    @Override
    public Executable getExecutable() {
        return executable;
    }

    @Override
    public ExecutionContext getContext() {
        return context;
    }

    @Override
    public Type getType() {
        return type;
    }
}
