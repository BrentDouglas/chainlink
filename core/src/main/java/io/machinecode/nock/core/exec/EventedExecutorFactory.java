package io.machinecode.nock.core.exec;

import io.machinecode.nock.spi.configuration.RuntimeConfiguration;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.execution.ExecutorFactory;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class EventedExecutorFactory implements ExecutorFactory {

    @Override
    public Executor produce(final RuntimeConfiguration configuration, final int threads) {
        return new EventedExecutor(configuration, threads);
    }
}
