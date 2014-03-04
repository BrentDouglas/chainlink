package io.machinecode.chainlink.cdi;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Named;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class NamedLiteral extends AnnotationLiteral<Named> implements Named {

    private final String value;

    public NamedLiteral(final String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }
}
