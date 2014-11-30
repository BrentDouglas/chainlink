package io.machinecode.chainlink.core.validation.task;

import io.machinecode.chainlink.core.validation.PropertyReferenceValidator;
import io.machinecode.chainlink.spi.element.task.ItemProcessor;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class ItemProcessorValidator extends PropertyReferenceValidator<ItemProcessor> {

    public static final ItemProcessorValidator INSTANCE = new ItemProcessorValidator();

    protected ItemProcessorValidator() {
        super(ItemProcessor.ELEMENT);
    }
}
