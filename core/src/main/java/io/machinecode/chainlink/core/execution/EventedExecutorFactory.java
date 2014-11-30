package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.spi.configuration.ExecutorConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.ExecutorFactory;
import io.machinecode.chainlink.spi.execution.Executor;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class EventedExecutorFactory implements ExecutorFactory {
    @Override
    public Executor produce(final ExecutorConfiguration configuration) {
        return new EventedExecutor(configuration);
    }
}
