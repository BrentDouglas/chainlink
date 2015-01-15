package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.spi.configuration.PropertyLookup;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class PropertyLookupImpl implements PropertyLookup {

    final Properties properties;

    public PropertyLookupImpl(final Properties properties) {
        this.properties = properties;
    }

    @Override
    public String getProperty(final String name) {
        return properties.getProperty(name);
    }

    @Override
    public String getProperty(final String name, final String defaultValue) {
        return properties.getProperty(name, defaultValue);
    }
}
