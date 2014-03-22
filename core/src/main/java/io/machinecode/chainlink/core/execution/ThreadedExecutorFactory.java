package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.ExecutorFactory;
import io.machinecode.chainlink.spi.execution.Executor;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ThreadedExecutorFactory implements ExecutorFactory {
    @Override
    public Executor produce(final Configuration configuration) {
        return new ThreadedExecutor(configuration);
    }
}
