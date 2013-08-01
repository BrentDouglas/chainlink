package io.machinecode.nock.jsl.validation;

import io.machinecode.nock.jsl.api.Batchlet;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class BatchletValidator extends PropertyReferenceValidator<Batchlet> {

    public static final BatchletValidator INSTANCE = new BatchletValidator();

    protected BatchletValidator() {
        super("batchlet");
    }
}
