package io.machinecode.chainlink.spi.schema;

import io.machinecode.chainlink.spi.management.Mutable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface MutableDeclarationSchema extends DeclarationSchema, Mutable<DeclarationSchema> {

    void setName(final String name);

    void setRef(final String ref);
}
