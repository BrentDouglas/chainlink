package io.machinecode.chainlink.spi.configuration.factory;

import io.machinecode.chainlink.spi.configuration.Configuration;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Factory<T, U extends Configuration> {

    T produce(final U configuration) throws Exception;
}
