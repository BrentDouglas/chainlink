package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.spi.configuration.ExecutorConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.ExecutorFactory;
import io.machinecode.chainlink.spi.execution.Executor;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ThreadedExecutorFactory implements ExecutorFactory {
    @Override
    public Executor produce(final ExecutorConfiguration configuration) {
        return new ThreadedExecutor(configuration);
    }
}
