package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.spi.configuration.FinalConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.WorkerFactory;
import io.machinecode.chainlink.spi.execution.Worker;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class EventedWorkerFactory implements WorkerFactory {
    @Override
    public Worker produce(final FinalConfiguration configuration) throws Exception {
        return new EventedWorker(configuration.getRuntimeConfiguration());
    }
}
