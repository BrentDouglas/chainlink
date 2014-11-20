package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.configuration.factory.WorkerFactory;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface FinalConfiguration extends WorkerConfiguration {

    WorkerFactory getWorkerFactory();

    RuntimeConfiguration getRuntimeConfiguration();
}
