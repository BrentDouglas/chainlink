package io.machinecode.chainlink.ee.wildfly.cdi;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Named;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
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
