package io.machinecode.chainlink.inject.guice;

import javax.batch.api.BatchProperty;
import java.io.Serializable;
import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class BatchPropertyLiteral implements BatchProperty, Serializable {
    private static final long serialVersionUID = 1L;

    public static final BatchPropertyLiteral DEFAULT = new BatchPropertyLiteral("");

    private final String name;

    public BatchPropertyLiteral(final String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return BatchProperty.class;
    }

    @Override
    public int hashCode() {
        return (127 * "name".hashCode()) ^ name.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof BatchProperty && name.equals(((BatchProperty) o).name());
    }
}
