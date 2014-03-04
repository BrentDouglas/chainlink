package io.machinecode.chainlink.spi.execution;

import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.deferred.Deferred;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExecutableEvent extends Serializable {

    Executable getExecutable();

    ExecutionContext getContext();
}