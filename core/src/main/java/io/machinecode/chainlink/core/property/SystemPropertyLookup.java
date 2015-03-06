package io.machinecode.chainlink.core.property;

import io.machinecode.chainlink.spi.property.PropertyLookup;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class SystemPropertyLookup implements PropertyLookup {

    public static final SystemPropertyLookup INSTANCE = new SystemPropertyLookup(EmptyPropertyLookup.INSTANCE);

    private final PropertyLookup lookup;

    public SystemPropertyLookup(final PropertyLookup lookup) {
        this.lookup = lookup;
    }

    @Override
    public String getProperty(final String name) {
        String that = System.getProperty(name);
        if (that != null) {
            return that;
        }
        return lookup.getProperty(name);
    }

    @Override
    public String getProperty(final String name, final String defaultValue) {
        final String that = this.getProperty(name);
        if (that != null) {
            return that;
        }
        return defaultValue;
    }
}
