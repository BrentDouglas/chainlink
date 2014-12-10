package io.machinecode.chainlink.spi.execution;

import io.machinecode.chainlink.spi.registry.ChainId;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface ExecutableEvent extends Serializable {

    ChainId getChainId();

    Executable getExecutable();
}