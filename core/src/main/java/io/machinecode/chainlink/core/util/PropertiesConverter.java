package io.machinecode.chainlink.core.util;

import io.machinecode.chainlink.spi.element.Properties;
import io.machinecode.chainlink.spi.element.Property;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
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
