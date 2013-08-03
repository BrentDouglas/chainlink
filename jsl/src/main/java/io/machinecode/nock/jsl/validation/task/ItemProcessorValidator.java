package io.machinecode.nock.jsl.validation.task;

import io.machinecode.nock.jsl.api.task.ItemProcessor;
import io.machinecode.nock.jsl.validation.PropertyReferenceValidator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemProcessorValidator extends PropertyReferenceValidator<ItemProcessor> {

    public static final ItemProcessorValidator INSTANCE = new ItemProcessorValidator();

    protected ItemProcessorValidator() {
        super("processor");
    }
}
