package io.machinecode.chainlink.core.registry;

import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.registry.ExecutableAndContext;

import java.io.Serializable;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class ExecutableAndContextImpl implements ExecutableAndContext, Serializable {
    private static final long serialVersionUID = 1L;

    private final Executable executable;
    private final ExecutionContext context;

    public ExecutableAndContextImpl(final Executable executable, final ExecutionContext context) {
        this.executable = executable;
        this.context = context;
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
