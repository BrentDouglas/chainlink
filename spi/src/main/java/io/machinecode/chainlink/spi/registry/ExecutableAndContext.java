package io.machinecode.chainlink.spi.registry;

import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public interface ExecutableAndContext {

    Executable getExecutable();

    ExecutionContext getContext();
}
