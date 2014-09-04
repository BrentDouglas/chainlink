package io.machinecode.chainlink.spi.registry;

import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public interface ExecutableAndContext {

    Executable getExecutable();

    ExecutionContext getContext();
}
