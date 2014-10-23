package io.machinecode.chainlink.spi.configuration.factory;

import io.machinecode.chainlink.spi.configuration.ConfigurationBuilder;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ConfigurationFactory {

    String getId();

    ConfigurationBuilder<?> produce() throws Exception;
}
