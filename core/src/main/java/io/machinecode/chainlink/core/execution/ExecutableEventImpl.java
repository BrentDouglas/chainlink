package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import io.machinecode.chainlink.spi.transport.DeferredId;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class ExecutableEventImpl implements ExecutableEvent {
    final DeferredId deferredId;
    final Executable executable;
    final ExecutionContext context;

    public ExecutableEventImpl(final Executable executable, final DeferredId deferredId, final ExecutionContext context) {
        this.deferredId = deferredId;
        this.executable = executable;
        this.context = context;
    }

    public ExecutableEventImpl(final Executable executable, final DeferredId deferredId) {
        this(executable, deferredId, null);
    }

    @Override
    public DeferredId getDeferredId() {
        return deferredId;
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
