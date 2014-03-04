package io.machinecode.chainlink.jsl.validation.task;

import io.machinecode.chainlink.spi.element.task.ItemProcessor;
import io.machinecode.chainlink.jsl.validation.PropertyReferenceValidator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemProcessorValidator extends PropertyReferenceValidator<ItemProcessor> {

    public static final ItemProcessorValidator INSTANCE = new ItemProcessorValidator();

    protected ItemProcessorValidator() {
        super(ItemProcessor.ELEMENT);
    }
}
