package io.machinecode.chainlink.spi.configuration.factory;

import io.machinecode.chainlink.spi.configuration.FinalConfiguration;
import io.machinecode.chainlink.spi.execution.Worker;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface WorkerFactory extends Factory<Worker, FinalConfiguration> {

}