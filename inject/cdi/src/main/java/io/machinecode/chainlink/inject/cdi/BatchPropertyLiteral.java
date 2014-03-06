package io.machinecode.chainlink.inject.cdi;

import javax.batch.api.BatchProperty;
import javax.enterprise.util.AnnotationLiteral;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class BatchPropertyLiteral extends AnnotationLiteral<BatchProperty> implements BatchProperty {

    public static final String DEFAULT_NAME = "";

    private final String name;

    public BatchPropertyLiteral(final String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }
}
