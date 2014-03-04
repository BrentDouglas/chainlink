package io.machinecode.chainlink.core.util;

import io.machinecode.chainlink.spi.element.Properties;
import io.machinecode.chainlink.spi.element.Property;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PropertiesConverter {

    public static java.util.Properties convert(final Properties properties) {
        final java.util.Properties ret = new java.util.Properties();
        for (final Property property : properties.getProperties()) {
            ret.put(property.getName(), property.getValue());
        }
        return ret;
    }
}
