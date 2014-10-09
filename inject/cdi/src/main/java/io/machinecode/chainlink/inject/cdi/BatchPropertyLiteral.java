package io.machinecode.chainlink.inject.cdi;

import javax.batch.api.BatchProperty;
import javax.enterprise.util.AnnotationLiteral;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class BatchPropertyLiteral extends AnnotationLiteral<BatchProperty> implements BatchProperty {
    private static final long serialVersionUID = 1L;

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
