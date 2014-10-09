package io.machinecode.chainlink.spi.execution;

import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableId;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface CallbackEvent extends Serializable {

    long getJobExecutionId();

    ChainId getChainId();

    ExecutableId getExecutableId();

    ExecutionContext getContext();
}