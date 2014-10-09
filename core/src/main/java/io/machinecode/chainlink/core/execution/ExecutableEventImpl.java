package io.machinecode.chainlink.core.execution;

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

    public ExecutableEventImpl(final Executable executable, final ChainId chainId) {
        this.chainId = chainId;
        this.executable = executable;
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
    public String toString() {
        final StringBuilder sb = new StringBuilder("ExecutableEventImpl{");
        sb.append("chainId=").append(chainId);
        sb.append(", executable=").append(executable);
        sb.append('}');
        return sb.toString();
    }
}
