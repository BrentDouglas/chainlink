package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.execution.ExecutorFactory;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class EventedExecutorFactory implements ExecutorFactory {

    @Override
    public Executor produce(final RuntimeConfiguration configuration, final int threads) {
        return new EventedExecutor(configuration, threads);
    }
}
