package io.machinecode.chainlink.spi.configuration;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface Configuration {

    String getProperty(final String key);

    String getProperty(final String key, final String defaultValue);

    Properties getProperties();
}
