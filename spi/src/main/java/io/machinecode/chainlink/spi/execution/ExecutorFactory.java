package io.machinecode.chainlink.spi.execution;

import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExecutorFactory {

    Executor produce(RuntimeConfiguration configuration, int threads);
}
