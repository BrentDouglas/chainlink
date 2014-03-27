package io.machinecode.chainlink.spi.configuration.factory;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.execution.Worker;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface WorkerFactory extends Factory<Worker, Configuration> {

}