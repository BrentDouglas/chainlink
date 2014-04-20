package io.machinecode.chainlink.spi.configuration.factory;

import io.machinecode.chainlink.spi.configuration.Configuration;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ConfigurationFactory {

    String getId();

    Configuration produce() throws Exception;
}
