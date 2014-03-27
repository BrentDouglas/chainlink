package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.factory.WorkerFactory;
import io.machinecode.chainlink.spi.execution.Worker;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class EventedWorkerFactory implements WorkerFactory {
    @Override
    public Worker produce(final Configuration configuration) throws Exception {
        return new EventedWorker(configuration.getRuntimeConfiguration());
    }
}
