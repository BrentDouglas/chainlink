package io.machinecode.chainlink.core.validation.partition;

import io.machinecode.chainlink.core.validation.PropertyReferenceValidator;
import io.machinecode.chainlink.spi.element.partition.Collector;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class CollectorValidator extends PropertyReferenceValidator<Collector> {

    public static final CollectorValidator INSTANCE = new CollectorValidator();

    protected CollectorValidator() {
        super(Collector.ELEMENT);
    }
}
