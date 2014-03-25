package io.machinecode.chainlink.spi.configuration;

import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface BaseConfiguration {

    String getProperty(final String key);

    Properties getProperties();
}
