package io.machinecode.chainlink.core.property;

import io.machinecode.chainlink.spi.property.PropertyLookup;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ArrayPropertyLookup implements PropertyLookup {

    final Properties[] properties;

    public ArrayPropertyLookup(final Properties... properties) {
        this.properties = properties;
    }

    @Override
    public String getProperty(final String name) {
        for (final Properties properties : this.properties) {
            String ret = properties.getProperty(name);
            if (ret != null) {
                return ret;
            }
        }
        return null;
    }

    @Override
    public String getProperty(final String name, final String defaultValue) {
        for (final Properties properties : this.properties) {
            String ret = properties.getProperty(name, defaultValue);
            if (ret != null) {
                return ret;
            }
        }
        return defaultValue;
    }
}
