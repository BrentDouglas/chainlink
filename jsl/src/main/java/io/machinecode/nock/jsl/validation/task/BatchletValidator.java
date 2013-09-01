package io.machinecode.nock.jsl.validation.task;

import io.machinecode.nock.spi.element.task.Batchlet;
import io.machinecode.nock.jsl.validation.PropertyReferenceValidator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class BatchletValidator extends PropertyReferenceValidator<Batchlet> {

    public static final BatchletValidator INSTANCE = new BatchletValidator();

    protected BatchletValidator() {
        super(Batchlet.ELEMENT);
    }
}
