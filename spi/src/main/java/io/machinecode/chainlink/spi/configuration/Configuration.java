package io.machinecode.chainlink.spi.configuration;

import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Configuration {

    String getProperty(final String key);

    String getProperty(final String key, final String defaultValue);

    Properties getProperties();
}
