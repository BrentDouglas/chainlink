package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.configuration.factory.WorkerFactory;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface FinalConfiguration extends WorkerConfiguration {

    WorkerFactory getWorkerFactory();

    RuntimeConfiguration getRuntimeConfiguration();
}
