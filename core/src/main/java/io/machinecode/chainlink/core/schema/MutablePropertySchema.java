package io.machinecode.chainlink.core.schema;

import io.machinecode.chainlink.spi.management.Mutable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface MutablePropertySchema extends PropertySchema, Mutable<PropertySchema> {

    void setName(final String name);

    void setValue(final String value);
}
