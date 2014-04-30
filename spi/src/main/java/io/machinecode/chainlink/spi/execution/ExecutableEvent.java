package io.machinecode.chainlink.spi.execution;

import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.registry.ChainId;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExecutableEvent extends Serializable {

    ChainId getChainId();

    Executable getExecutable();

    ExecutionContext getContext();
}