package io.machinecode.chainlink.core.validation.task;

import io.machinecode.chainlink.core.validation.PropertyReferenceValidator;
import io.machinecode.chainlink.spi.jsl.task.Batchlet;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class BatchletValidator extends PropertyReferenceValidator<Batchlet> {

    public static final BatchletValidator INSTANCE = new BatchletValidator();

    protected BatchletValidator() {
        super(Batchlet.ELEMENT);
    }
}
