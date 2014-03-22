package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.execution.Executor;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExecutorFactory {

    Executor produce(final Configuration configuration);
}
