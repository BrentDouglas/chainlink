package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.ExecutorFactory;
import io.machinecode.chainlink.spi.execution.Executor;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class EventedExecutorFactory implements ExecutorFactory {

    final ThreadFactoryLookup threadFactory;

    public EventedExecutorFactory() {
        this(new DefaultThreadFactory());
    }

    public EventedExecutorFactory(final ThreadFactoryLookup threadFactory) {
        this.threadFactory = threadFactory;
    }

    @Override
    public Executor produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
        return new EventedExecutor(dependencies, properties, this.threadFactory.lookupThreadFactory(properties));
    }
}
