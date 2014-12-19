package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import io.machinecode.chainlink.spi.registry.ChainId;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class ExecutableEventImpl implements ExecutableEvent {
    private static final long serialVersionUID = 1L;

    final ChainId chainId;
    final Executable executable;
    final ExecutionContext context;

    public ExecutableEventImpl(final Executable executable, final ChainId chainId, final ExecutionContext context) {
        this.chainId = chainId;
        this.executable = executable;
        this.context = context;
    }

    public ExecutableEventImpl(final Executable executable, final ChainId chainId) {
        this(executable, chainId, null);
    }

    @Override
    public ChainId getChainId() {
        return chainId;
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
