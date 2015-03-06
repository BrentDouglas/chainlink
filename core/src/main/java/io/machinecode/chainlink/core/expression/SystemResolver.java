package io.machinecode.chainlink.core.expression;

import io.machinecode.chainlink.core.property.SystemPropertyLookup;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
class SystemResolver extends Resolver {
    final SystemPropertyLookup properties;

    SystemResolver(final SystemPropertyLookup properties) {
        super(Expression.SYSTEM_PROPERTIES, Expression.SYSTEM_PROPERTIES_LENGTH);
        this.properties = properties;
    }

    CharSequence resolve(final CharSequence value) {
        assert this.properties != null;
        final String that = this.properties.getProperty(value.toString());
        return that == null ? EMPTY : that;
    }
}
