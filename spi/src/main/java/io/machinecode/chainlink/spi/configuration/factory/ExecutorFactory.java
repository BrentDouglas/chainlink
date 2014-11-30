package io.machinecode.chainlink.spi.configuration.factory;

import io.machinecode.chainlink.spi.configuration.ExecutorConfiguration;
import io.machinecode.chainlink.spi.execution.Executor;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface ExecutorFactory extends Factory<Executor, ExecutorConfiguration> {

}
