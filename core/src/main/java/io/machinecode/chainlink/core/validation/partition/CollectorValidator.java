package io.machinecode.chainlink.core.validation.partition;

import io.machinecode.chainlink.core.validation.PropertyReferenceValidator;
import io.machinecode.chainlink.spi.element.partition.Collector;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CollectorValidator extends PropertyReferenceValidator<Collector> {

    public static final CollectorValidator INSTANCE = new CollectorValidator();

    protected CollectorValidator() {
        super(Collector.ELEMENT);
    }
}
