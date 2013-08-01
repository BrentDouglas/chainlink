package io.machinecode.nock.jsl.validation.partition;

import io.machinecode.nock.jsl.api.partition.Collector;
import io.machinecode.nock.jsl.validation.PropertyReferenceValidator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CollectorValidator extends PropertyReferenceValidator<Collector> {

    public static final CollectorValidator INSTANCE = new CollectorValidator();

    protected CollectorValidator() {
        super("collector");
    }
}
