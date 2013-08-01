package io.machinecode.nock.jsl.validation.chunk;

import io.machinecode.nock.jsl.api.chunk.ItemProcessor;
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
