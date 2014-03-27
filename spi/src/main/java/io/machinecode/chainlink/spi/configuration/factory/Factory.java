package io.machinecode.chainlink.spi.configuration.factory;

import io.machinecode.chainlink.spi.configuration.BaseConfiguration;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Factory<T, U extends BaseConfiguration> {

    T produce(final U configuration) throws Exception;
}
