package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.ExecutorFactory;
import io.machinecode.chainlink.spi.execution.Executor;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class EventedExecutorFactory implements ExecutorFactory {
    @Override
    public Executor produce(final Dependencies dependencies, final Properties properties) {
        return new EventedExecutor(dependencies, properties);
    }
}
