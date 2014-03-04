package io.machinecode.chainlink.jsl.validation.partition;

import io.machinecode.chainlink.spi.element.partition.Collector;
import io.machinecode.chainlink.jsl.validation.PropertyReferenceValidator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CollectorValidator extends PropertyReferenceValidator<Collector> {

    public static final CollectorValidator INSTANCE = new CollectorValidator();

    protected CollectorValidator() {
        super(Collector.ELEMENT);
    }
}
