package io.machinecode.chainlink.core.expression;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
class PropertyResolver extends Resolver {
    final Properties properties;

    PropertyResolver(final String prefix, final int length, final Properties properties) {
        super(prefix, length);
        this.properties = properties;
    }

    CharSequence resolve(final CharSequence value) {
        if (this.properties == null) {
            return EMPTY;
        }
        final String that = this.properties.getProperty(value.toString());
        return that == null ? EMPTY : that;
    }
}
