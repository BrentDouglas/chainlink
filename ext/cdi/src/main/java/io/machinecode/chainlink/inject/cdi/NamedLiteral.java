package io.machinecode.chainlink.inject.cdi;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Named;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class NamedLiteral extends AnnotationLiteral<Named> implements Named {
    private static final long serialVersionUID = 1L;

    private final String value;

    public NamedLiteral(final String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }
}
