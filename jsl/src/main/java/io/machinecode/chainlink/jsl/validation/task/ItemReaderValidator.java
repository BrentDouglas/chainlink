package io.machinecode.chainlink.jsl.validation.task;

import io.machinecode.chainlink.spi.element.task.ItemReader;
import io.machinecode.chainlink.jsl.validation.PropertyReferenceValidator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemReaderValidator extends PropertyReferenceValidator<ItemReader> {

    public static final ItemReaderValidator INSTANCE = new ItemReaderValidator();

    protected ItemReaderValidator() {
        super(ItemReader.ELEMENT);
    }
}
