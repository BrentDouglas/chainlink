package io.machinecode.chainlink.core.property;

import io.machinecode.chainlink.spi.property.PropertyLookup;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class EmptyPropertyLookup implements PropertyLookup {

    public static final EmptyPropertyLookup INSTANCE = new EmptyPropertyLookup();

    @Override
    public String getProperty(final String name) {
        return null;
    }

    @Override
    public String getProperty(final String name, final String defaultValue) {
        return defaultValue;
    }
}
