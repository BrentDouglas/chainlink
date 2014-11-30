package io.machinecode.chainlink.core.validation.task;

import io.machinecode.chainlink.core.validation.PropertyReferenceValidator;
import io.machinecode.chainlink.spi.element.task.ItemReader;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class ItemReaderValidator extends PropertyReferenceValidator<ItemReader> {

    public static final ItemReaderValidator INSTANCE = new ItemReaderValidator();

    protected ItemReaderValidator() {
        super(ItemReader.ELEMENT);
    }
}
