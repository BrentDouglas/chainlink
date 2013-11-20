package io.machinecode.nock.spi.execution;

import io.machinecode.nock.spi.configuration.RuntimeConfiguration;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExecutorFactory {

    Executor produce(RuntimeConfiguration configuration, int threads);
}
